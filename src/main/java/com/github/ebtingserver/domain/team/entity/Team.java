package com.github.ebtingserver.domain.team.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Team extends com.github.ebtingserver.common.entity.BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="team_id", nullable = false)
    private long teamId;

    @Column(name = "team_name", nullable = false)
    private String teamName;

    @Column(name = "max_member")
    private Integer maxMember;

    @Column(name="team_explain")
    private String teamExplain;
}
