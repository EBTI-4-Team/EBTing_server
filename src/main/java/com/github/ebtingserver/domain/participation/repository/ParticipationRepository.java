package com.github.ebtingserver.domain.participation.repository;

import com.github.ebtingserver.domain.participation.entity.Participation;
import org.springframework.data.jpa.repository.EntityGraph;
import com.github.ebtingserver.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ParticipationRepository extends JpaRepository<Participation, Long> {
    List<Participation> findByUser(User user);
public interface ParticipationRepository extends JpaRepository<Participation, Integer> {
    @EntityGraph(attributePaths = "user")
    List<Participation> findByTeam_TeamId(long teamTeamId);
}
