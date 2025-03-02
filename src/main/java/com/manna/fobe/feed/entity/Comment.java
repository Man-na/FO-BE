package com.manna.fobe.feed.entity;

import com.manna.fobe.common.entity.CommonEntity;
import com.manna.fobe.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "comments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment extends CommonEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String content;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "feed_id", nullable = false)
    private Feed feed;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Comment parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private List<Comment> replies;
}