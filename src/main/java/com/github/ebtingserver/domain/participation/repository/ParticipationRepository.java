package com.github.ebtingserver.domain.participation.repository;

import com.github.ebtingserver.domain.participation.entity.Participation;
import com.github.ebtingserver.domain.participation.entity.ParticipationRole;
import com.github.ebtingserver.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ParticipationRepository extends JpaRepository<Participation, Long> {
    List<Participation> findByUser(User user);
    List<Participation> findByTeam_TeamId(Long teamId);
    boolean existsByTeam_TeamIdAndUser_UserIdAndRole(Long teamId, Long userId, ParticipationRole role);
    long deleteByTeam_TeamIdAndUser_UserId(Long teamId, Long userId);
}
