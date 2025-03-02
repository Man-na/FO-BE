package com.manna.fobe.feed.entity;

import com.manna.fobe.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_votes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserVote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "vote_option_id", nullable = false)
    private VoteOption voteOption;
}