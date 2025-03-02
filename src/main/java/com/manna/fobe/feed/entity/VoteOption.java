package com.manna.fobe.feed.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "vote_options")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoteOption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int displayPriority; // 표시 우선순위
    private String content; // 옵션 내용

    @ManyToOne
    @JoinColumn(name = "vote_id", nullable = false)
    private Vote vote; // 옵션이 속한 투표

    @OneToMany(mappedBy = "voteOption", cascade = CascadeType.ALL)
    private List<UserVote> userVotes; // 사용자가 선택한 투표 기록
}