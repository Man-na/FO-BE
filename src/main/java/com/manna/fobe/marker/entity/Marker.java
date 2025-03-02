package com.manna.fobe.marker.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.manna.fobe.common.entity.CommonEntity;
import com.manna.fobe.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "posts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Marker extends CommonEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String title;
    private String address;

    @Column(name = "date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate date;

    private String description;
    private double latitude;
    private double longitude;
    private String color;
    private int score;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "marker", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Image> images;
}