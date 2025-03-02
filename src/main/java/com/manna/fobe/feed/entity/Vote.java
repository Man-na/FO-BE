package com.manna.fobe.feed.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "votes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String title;

    @ManyToOne
    @JoinColumn(name = "feed_id", nullable = false)
    private Feed feed;

    @OneToMany(mappedBy = "vote", cascade = CascadeType.ALL)
    private List<VoteOption> options;
}