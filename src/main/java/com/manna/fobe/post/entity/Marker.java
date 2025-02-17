package com.manna.fobe.post.entity;

import com.manna.fobe.common.entity.CommonEntity;
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
}
