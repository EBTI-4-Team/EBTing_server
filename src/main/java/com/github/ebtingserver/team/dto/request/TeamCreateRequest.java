package com.github.ebtingserver.team.dto.request;

public record TeamCreateRequest(
        String teamName,
        Integer maxMember,
        String teamExplain
) {
}
