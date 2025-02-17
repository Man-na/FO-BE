package com.manna.fobe.post.entity;

import com.manna.fobe.common.entity.CommonEntity;
import com.manna.fobe.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "markers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Marker extends CommonEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private double latitude;
    private double longitude;
    private String color;
    private int score;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
