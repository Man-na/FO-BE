package com.manna.fobe.feed.entity;

import com.manna.fobe.common.entity.CommonEntity;
import com.manna.fobe.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "feeds")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Feed extends CommonEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String title;

    private String description;

    private int categoryId;

    @Column(name = "user_id", insertable = false, updatable = false)
    private int userId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User author;

    @OneToMany(mappedBy = "feed", cascade = CascadeType.ALL)
    private List<Comment> comments;

    @OneToMany(mappedBy = "feed", cascade = CascadeType.ALL)
    private List<Vote> votes;

    @OneToMany(mappedBy = "feed", cascade = CascadeType.ALL)
    private List<Like> likes;

    private boolean hasVote;
    private int voteCount;
    private int commentCount;
    private int viewCount;
}