package com.github.ebtingserver.domain.team.repository;

import com.github.ebtingserver.domain.team.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<Team, Long> {
}
