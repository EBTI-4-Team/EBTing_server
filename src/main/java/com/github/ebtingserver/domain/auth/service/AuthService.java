package com.github.ebtingserver.domain.auth.service;

import com.github.ebtingserver.common.exception.CustomException;
import com.github.ebtingserver.common.util.JwtUtil;
import com.github.ebtingserver.domain.auth.dto.LoginRequestDto;
import com.github.ebtingserver.domain.auth.dto.LoginResponseDto;
import com.github.ebtingserver.domain.auth.dto.SignupRequestDto;
import com.github.ebtingserver.domain.auth.exception.AuthExceptionCode;
import com.github.ebtingserver.domain.user.entity.User;
import com.github.ebtingserver.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional
    public void signup(SignupRequestDto requestDto) {
        // 전화번호 중복 체크
        if (userRepository.existsByPhoneNumber(requestDto.getPhonenumber())) {
            throw new CustomException(AuthExceptionCode.DUPLICATE_PHONENUMBER);
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(requestDto.getPassword());

        // 사용자 저장
        User user = User.builder()
                .name(requestDto.getName())
                .phoneNumber(requestDto.getPhonenumber())
                .password(encodedPassword)
                .build();

        userRepository.save(user);
    }

    @Transactional
    public LoginResponseDto login(LoginRequestDto requestDto) {
        // 사용자 찾기
        User user = userRepository.findByPhoneNumber(requestDto.getPhonenumber())
                .orElseThrow(() -> new CustomException(AuthExceptionCode.USER_NOT_FOUND));

        // 비밀번호 검증
        if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
            throw new CustomException(AuthExceptionCode.INVALID_PASSWORD);
        }

        // JWT 토큰 생성
        String accessToken = jwtUtil.generateAccessToken(user.getUserId());

        return LoginResponseDto.builder()
                .accessToken(accessToken)
                .userId(user.getUserId())
                .name(user.getName())
                .build();
    }

}
