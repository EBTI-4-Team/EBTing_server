package com.github.ebtingserver.domain.team.repository;

import com.github.ebtingserver.domain.team.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReportRepository extends JpaRepository<Report, Long> {
    Optional<Report> findByTeam_TeamId(Long teamId);
    boolean existsByTeam_TeamId(Long teamId);
}
