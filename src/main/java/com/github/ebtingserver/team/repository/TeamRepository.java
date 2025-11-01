package com.github.ebtingserver.team.repository;

import com.github.ebtingserver.team.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<Team, Long> {
}
