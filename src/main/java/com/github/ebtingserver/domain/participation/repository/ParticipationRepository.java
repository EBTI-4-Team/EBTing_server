package com.github.ebtingserver.domain.participation.repository;

import com.github.ebtingserver.domain.participation.entity.Participation;
import com.github.ebtingserver.domain.participation.entity.ParticipationRole;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ParticipationRepository extends JpaRepository<Participation, Long> {
    @EntityGraph(attributePaths = "user")
    List<Participation> findByTeam_TeamId(long teamTeamId);

    boolean existsByTeam_TeamIdAndUser_UserIdAndRole(Long teamId, Long userId, ParticipationRole role);

    long deleteByTeam_TeamId(Long teamId);
}
