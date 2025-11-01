package com.github.ebtingserver.domain.team.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.ebtingserver.common.exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import com.github.ebtingserver.domain.participation.entity.Participation;
import com.github.ebtingserver.domain.participation.entity.ParticipationRole;
import com.github.ebtingserver.domain.participation.repository.ParticipationRepository;
import com.github.ebtingserver.domain.team.dto.request.TeamCreateRequest;
import com.github.ebtingserver.domain.team.dto.request.TeamInfoUpdate;
import com.github.ebtingserver.domain.team.dto.response.TeamDetailResponse;
import com.github.ebtingserver.domain.team.dto.response.TeamMemberResponse;
import com.github.ebtingserver.domain.team.dto.response.TeamReportResponse;
import com.github.ebtingserver.domain.team.dto.response.TeamResponseDto;
import com.github.ebtingserver.domain.team.entity.Report;
import com.github.ebtingserver.domain.team.entity.Team;
import com.github.ebtingserver.domain.team.repository.ReportRepository;
import com.github.ebtingserver.domain.team.repository.TeamRepository;
import com.github.ebtingserver.domain.user.entity.User;
import com.github.ebtingserver.domain.user.exception.UserExceptionCode;
import com.github.ebtingserver.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TeamService {
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final ParticipationRepository participationRepository;
    private final ReportRepository reportRepository;
    private final ObjectMapper objectMapper;

    @Value("${openai.api-key}")
    private String openaiApiKey;

    @Value("${openai.api-url}")
    private String openaiApiUrl;

    @Value("${openai.model}")
    private String openaiModel;

    public List<TeamResponseDto> getAllTeams() {
        return teamRepository.findAll()
                .stream()
                .map(team -> {
                    List<TeamMemberResponse> members = participationRepository.findByTeam_TeamId(team.getTeamId())
                            .stream()
                            .map(p -> new TeamMemberResponse(
                                    p.getUser().getUserId(),
                                    p.getUser().getName(),
                                    p.getUser().getEbti()
                            ))
                            .toList();
                    return TeamResponseDto.from(team, members);
                })
                .toList();
    }

    @Transactional
    public void createTeam(Long userId, TeamCreateRequest request){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(UserExceptionCode.USER_NOT_FOUND));

        Team team = Team.builder()
                .teamName(request.teamName())
                .maxMember(request.maxMember())
                .teamExplain(request.teamExplain())
                .build();

        teamRepository.save(team);

        Participation participation = Participation.builder()
                .user(user)
                .team(team)
                .role(ParticipationRole.ADMIN)
                .build();
        participationRepository.save(participation);
    }

    @Transactional
    public TeamDetailResponse getTeamDetail(Long teamId){
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "팀을 찾을 수 없습니다"));
        List<TeamMemberResponse> members = participationRepository.findByTeam_TeamId(teamId)
                .stream()
                .map(p -> new TeamMemberResponse(
                        p.getUser().getUserId(),
                        p.getUser().getName(),
                        p.getUser().getEbti()
                ))
                .toList();

        return TeamDetailResponse.of(team, members);
    }

    @Transactional
    public void updateTeamInfo(Long teamId, TeamInfoUpdate request){
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "팀을 찾을 수 없습니다"));
        team.update(request.teamName(), request.maxMember(), request.teamExplain());
    }

    @Transactional
    public void deleteTeam(Long teamId, Long userId){
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "팀을 찾을 수 없습니다"));
        boolean isAdmin = participationRepository
                .existsByTeam_TeamIdAndUser_UserIdAndRole(teamId, userId, ParticipationRole.ADMIN);
        if (!isAdmin) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "삭제 권한이 없습니다 (ADMIN 전용)");
        }

        // 팀원 확인 - MEMBER가 있으면 삭제 불가
        List<Participation> participations = participationRepository.findByTeam_TeamId(teamId);
        boolean hasMember = participations.stream()
                .anyMatch(p -> p.getRole() == ParticipationRole.MEMBER);

        if (hasMember) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "팀원이 있어 삭제할 수 없습니다. 먼저 모든 팀원을 내보내주세요");
        }

        // ADMIN만 있을 경우 삭제 진행
        participationRepository.deleteAll(participations);
        teamRepository.deleteById(teamId);
    }

    @Transactional
    public void joinTeam(Long teamId, Long userId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "팀을 찾을 수 없습니다"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(UserExceptionCode.USER_NOT_FOUND));

        // 이미 가입된 팀원인지 확인
        List<Participation> existingParticipations = participationRepository.findByTeam_TeamId(teamId);
        boolean alreadyJoined = existingParticipations.stream()
                .anyMatch(p -> p.getUser().getUserId() == userId);

        if (alreadyJoined) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이미 가입된 팀입니다");
        }

        // 최대 인원 체크
        if (team.getMaxMember() != null && existingParticipations.size() >= team.getMaxMember()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "팀 인원이 가득 찼습니다");
        }

        // MEMBER 역할로 참가
        Participation participation = Participation.builder()
                .user(user)
                .team(team)
                .role(ParticipationRole.MEMBER)
                .build();
        participationRepository.save(participation);
    }

    @Transactional
    public void leaveTeam(Long teamId, Long userId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "팀을 찾을 수 없습니다"));

        // ADMIN은 탈퇴 불가
        boolean isAdmin = participationRepository
                .existsByTeam_TeamIdAndUser_UserIdAndRole(teamId, userId, ParticipationRole.ADMIN);

        if (isAdmin) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                "ADMIN은 팀을 탈퇴할 수 없습니다. 팀을 삭제해주세요");
        }

        // 참가 기록 삭제
        long deletedCount = participationRepository.deleteByTeam_TeamIdAndUser_UserId(teamId, userId);

        if (deletedCount == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 팀에 가입되어 있지 않습니다");
        }
    }

    @Transactional
    public TeamReportResponse generateReport(Long teamId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "팀을 찾을 수 없습니다"));

        List<Participation> participations = participationRepository.findByTeam_TeamId(teamId);

        if (participations.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "팀원이 없습니다");
        }

        // 팀원들의 EBTI 정보 수집
        String teamMembersInfo = participations.stream()
                .map(p -> {
                    User user = p.getUser();
                    String ebti = user.getEbti() != null ? user.getEbti() : "미설정";
                    return String.format("- %s (%s): EBTI %s", user.getName(), p.getRole(), ebti);
                })
                .collect(Collectors.joining("\n"));

        // GPT 프롬프트 생성
        String prompt = buildPrompt(team, teamMembersInfo);

        // OpenAI API 호출
        String reportContent = callOpenAI(prompt);

        // DB에 저장 (이미 있으면 업데이트, 없으면 생성)
        Report report = reportRepository.findByTeam_TeamId(teamId)
                .map(existingReport -> {
                    existingReport.updateContent(reportContent);
                    return existingReport;
                })
                .orElseGet(() -> {
                    Report newReport = Report.builder()
                            .team(team)
                            .reportContent(reportContent)
                            .build();
                    return reportRepository.save(newReport);
                });

        return TeamReportResponse.from(report, participations.size());
    }

    @Transactional(readOnly = true)
    public TeamReportResponse getReport(Long teamId, Long reportId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "팀을 찾을 수 없습니다"));

        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "리포트를 찾을 수 없습니다"));

        // reportId가 해당 teamId와 일치하는지 확인
        if (report.getTeam().getTeamId() != teamId) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "해당 팀의 리포트가 아닙니다");
        }

        int memberCount = participationRepository.findByTeam_TeamId(teamId).size();

        return TeamReportResponse.from(report, memberCount);
    }

    private String buildPrompt(Team team, String teamMembersInfo) {
        StringBuilder prompt = new StringBuilder();

        // EBTI 소개
        prompt.append("=== EBTI란? ===\n");
        prompt.append("EBTI는 'Entrepreneurial Behavior Type Indicator'의 약자로, ");
        prompt.append("개인의 기업가적 사고와 행동을 분석하여 자기다운 성장과 협업의 방향을 제시하는 기업가정신 진단 도구입니다.\n\n");

        prompt.append("EBTI는 4가지 기업가행동유형으로 구분됩니다:\n");
        prompt.append("1. 발견자(Discoverer): 시장과 기회를 탐색하고 발견하는 데 강점을 가진 유형\n");
        prompt.append("2. 혁신자(Innovator): 새로운 아이디어와 기술로 혁신을 주도하는 유형\n");
        prompt.append("3. 창조자(Creator): 독창적인 가치를 창출하고 실현하는 유형\n");
        prompt.append("4. 균형자(Equilibriator): 안정과 지속가능성을 추구하며 균형을 유지하는 유형\n\n");

        prompt.append("EBTI는 8가지 핵심 기업가행동특성과 결합하여 총 24가지 세부 유형으로 확장되며, ");
        prompt.append("개인과 팀의 창의·혁신 역량을 구체적으로 진단합니다.\n\n");

        prompt.append("=== 24가지 EBTI 세부 유형 ===\n");
        prompt.append("[발견자(Discoverer) 유형]\n");
        prompt.append("1. DICE — 바른주의 발견자: 서로 다른 것을 연결하고 새로운 것을 잘 찾는 발견자형. 내면에 창조자 감각이 있고 균형자 감각이 부족\n");
        prompt.append("2. DIEC — 경험주의 발견자: 서로 다른 것을 연결하고 새로운 것을 잘 찾는 발견자형. 내면에 균형자 감각이 있고 창조자 감각이 부족\n");
        prompt.append("3. DCIE — 기획하는 발견자: 선도적으로 헌신하며 새로운 것을 잘 찾는 발견자형. 내면에 혁신자 감각이 있고 균형자 감각이 부족\n");
        prompt.append("4. DCEI — 트렌드 발견자: 서로 다른 것을 연결하고 새로운 것을 잘 찾는 유형. 내면에 균형자 감각이 있고 혁신자 감각이 부족\n");
        prompt.append("5. DEIC — 일잘러 발견자: 새로움을 잘 찾고 실행력이 뛰어난 발견자형. 내면에 혁신자 감각이 있고 창조자 감각이 부족\n");
        prompt.append("6. DECI — 호기심현실 발견자: 호기심 많고 현실적이며 새로움을 찾는 발견자형. 내면에 창조자 감각이 있고 혁신자 감각이 부족\n\n");

        prompt.append("[혁신자(Innovator) 유형]\n");
        prompt.append("7. IDCE — 뒤에있는 혁신자: 새로운 것을 잘 찾고 다름을 융합하는 혁신자형. 내면에 창조자 감각이 있고 균형자 감각이 부족\n");
        prompt.append("8. IDEC — 결과예측 혁신자: 새로움을 잘 찾고 예측력이 좋은 혁신자형. 내면에 균형자 감각이 있고 창조자 감각이 부족\n");
        prompt.append("9. ICDE — 힙쿨스터 혁신자: 선도적으로 헌신하며 융합적인 혁신자형. 내면에 발견자 감각이 있고 균형자 감각이 부족\n");
        prompt.append("10. ICED — 신기방기 혁신자: 선도적이며 다양한 시도를 즐기는 혁신자형. 내면에 균형자 감각이 있고 발견자 감각이 부족\n");
        prompt.append("11. IEDC — 균형찾는 혁신자: 다름을 융합하며 조화를 추구하는 혁신자형. 내면에 발견자 감각이 있고 창조자 감각이 부족\n");
        prompt.append("12. IECD — 이종결합 혁신자: 다름을 융합하며 독창적인 결과를 만드는 혁신자형. 내면에 창조자 감각이 있고 발견자 감각이 부족\n\n");

        prompt.append("[창조자(Creator) 유형]\n");
        prompt.append("13. CDIE — 개척하는 창조자: 새로운 것을 선도적으로 추진하는 창조자형. 내면에 혁신자 감각이 있고 균형자 감각이 부족\n");
        prompt.append("14. CDEI — 열정호기심 창조자: 새로움과 열정을 동시에 추구하는 창조자형. 내면에 균형자 감각이 있고 혁신자 감각이 부족\n");
        prompt.append("15. CIDE — 독창주의 창조자: 다름을 융합하며 독창성을 발휘하는 창조자형. 내면에 발견자 감각이 있고 균형자 감각이 부족\n");
        prompt.append("16. CIED — 열정있는 창조자: 선도적이며 융합적인 열정형 창조자. 내면에 균형자 감각이 있고 발견자 감각이 부족\n");
        prompt.append("17. CEDI — 신중대담 창조자: 신중하면서도 실행력이 강한 창조자형. 내면에 발견자 감각이 있고 혁신자 감각이 부족\n");
        prompt.append("18. CEID — 용기있는 창조자: 선도적으로 헌신하며 실행력이 강한 창조자형. 내면에 혁신자 감각이 있고 발견자 감각이 부족\n\n");

        prompt.append("[균형자(Equilibriator) 유형]\n");
        prompt.append("19. EDIC — 미식가 균형자: 새로움을 잘 찾고 좋음을 선별하는 균형자형. 내면에 창조자 감각이 있고 혁신자 감각이 부족\n");
        prompt.append("20. EDCI — 명랑새롬 균형자: 새로움을 즐기며 조화롭게 행동하는 균형자형. 내면에 혁신자 감각이 있고 창조자 감각이 부족\n");
        prompt.append("21. ECDI — 긍정하는 균형자: 헌신적이고 조화로운 균형자형. 내면에 발견자 감각이 있고 혁신자 감각이 부족\n");
        prompt.append("22. ECID — 가치선별 균형자: 선도적으로 행동하며 옳고 그름을 잘 구분하는 균형자형. 내면에 혁신자 감각이 있고 발견자 감각이 부족\n");
        prompt.append("23. EIDC — 해법찾는 균형자: 다름을 융합하고 해법을 찾는 균형자형. 내면에 발견자 감각이 있고 창조자 감각이 부족\n");
        prompt.append("24. EICD — 판단귀재 균형자: 다름을 융합하고 냉철한 판단을 하는 균형자형. 내면에 창조자 감각이 있고 발견자 감각이 부족\n\n");

        prompt.append("=== 당신의 역할 ===\n");
        prompt.append("당신은 EBTI 전문 팀 프로젝트 컨설턴트입니다. ");
        prompt.append("다음 팀의 EBTI 정보를 바탕으로 프로젝트 진행 방법을 제안해주세요.\n\n");

        prompt.append("=== 팀 정보 ===\n");
        prompt.append("팀명: ").append(team.getTeamName()).append("\n\n");

        prompt.append("=== 팀원 구성 ===\n");
        prompt.append(teamMembersInfo).append("\n\n");

        prompt.append("=== 프로젝트 정보 ===\n");
        if (team.getTeamExplain() != null && !team.getTeamExplain().isBlank()) {
            prompt.append("프로젝트 내용: ").append(team.getTeamExplain()).append("\n");
        } else {
            prompt.append("프로젝트 내용: (팀 설명이 없습니다)\n");
        }

        prompt.append("\n위 정보를 바탕으로 다음 형식을 정확히 따라서 팀 리포트를 작성해주세요.\n\n");
        prompt.append("**중요 지침:**\n");
        prompt.append("1. 반드시 아래 형식 그대로 작성하고, 각 섹션 제목과 하위 항목을 정확히 포함해주세요.\n");
        prompt.append("2. 전체 리포트는 **2000자 이내**로 작성해주세요.\n");
        prompt.append("3. 각 섹션은 간결하고 핵심적인 내용만 포함하되, 실용적이고 구체적으로 작성해주세요.\n\n");

        prompt.append("1. 팀 구성원 EBTI 분석\n");
        prompt.append("- 각 팀원의 EBTI 유형별 특징과 강점\n");
        prompt.append("- 발견자/혁신자/창조자/균형자 유형의 분포와 의미\n\n");

        prompt.append("2. 팀의 강점과 약점\n");
        prompt.append("- 현재 팀 구성의 강점 (EBTI 유형 조합 기준)\n");
        prompt.append("- 보완이 필요한 영역과 약점\n\n");

        prompt.append("3. 효과적인 협업 방법\n");
        prompt.append("- EBTI 유형별 최적의 커뮤니케이션 방법\n");
        prompt.append("- 팀 시너지를 극대화하는 협업 전략\n\n");

        prompt.append("4. 역할 분담 제안\n");
        prompt.append("- 각 팀원의 EBTI 유형에 맞는 최적의 역할\n");
        prompt.append("- 프로젝트 단계별 책임 분담 방안\n\n");

        prompt.append("5. 프로젝트 진행 시 주의사항\n");
        prompt.append("- EBTI 유형별로 발생 가능한 갈등 상황과 해결 방안\n");
        prompt.append("- 팀 성과를 높이기 위한 실천 가능한 조언\n\n");

        prompt.append("리포트는 한국어로 작성하되, 위 형식을 정확히 지키고 구체적이고 실용적이며 즉시 적용 가능한 조언을 제공해주세요. ");
        prompt.append("각 섹션 번호와 제목, 하위 항목(-)을 모두 포함하여 작성해주세요.");

        return prompt.toString();
    }

    private String callOpenAI(String prompt) {
        try {
            URL url = new URL(openaiApiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "Bearer " + openaiApiKey);
            conn.setDoOutput(true);

            // Request body 생성
            String requestBody = String.format("""
                {
                    "model": "%s",
                    "messages": [
                        {
                            "role": "user",
                            "content": %s
                        }
                    ],
                    "max_completion_tokens": 16000
                }
                """, openaiModel, objectMapper.writeValueAsString(prompt));

            // 요청 전송
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = requestBody.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // 응답 처리
            int responseCode = conn.getResponseCode();

            if (responseCode != 200) {
                // 에러 응답 읽기
                StringBuilder errorResponse = new StringBuilder();
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        errorResponse.append(line);
                    }
                } catch (Exception ignored) {}

                // 429 Rate Limit 에러 처리
                if (responseCode == 429) {
                    throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS,
                            "OpenAI API 요청 한도를 초과했습니다. 잠시 후 다시 시도하거나 API 키의 크레딧을 확인해주세요.");
                }

                // 401 인증 에러 처리
                if (responseCode == 401) {
                    throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                            "OpenAI API 키가 유효하지 않습니다. API 키를 확인해주세요.");
                }

                // 기타 에러
                String errorMsg = errorResponse.length() > 0 ? errorResponse.toString() : "알 수 없는 오류";
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                        "OpenAI API 호출 실패 (" + responseCode + "): " + errorMsg);
            }

            StringBuilder response = new StringBuilder();
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
            }

            // JSON 파싱하여 content 추출
            String responseStr = response.toString();
            log.info("OpenAI API 응답: {}", responseStr);

            if (responseStr.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                        "OpenAI API 응답이 비어있습니다");
            }

            JsonNode jsonNode = objectMapper.readTree(responseStr);

            // choices 배열이 있는지 확인
            if (!jsonNode.has("choices") || jsonNode.get("choices").isEmpty()) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                        "OpenAI API 응답 형식이 올바르지 않습니다: " + responseStr);
            }

            JsonNode contentNode = jsonNode.get("choices").get(0).get("message").get("content");
            if (contentNode == null || contentNode.isNull()) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                        "OpenAI API 응답에 content가 없습니다: " + responseStr);
            }

            String reportContent = contentNode.asText();
            log.info("추출된 리포트 내용 길이: {}", reportContent.length());

            return reportContent;

        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "리포트 생성 중 오류가 발생했습니다: " + e.getMessage());
        }
    }


}
