package com.manna.fobe.post.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.manna.fobe.common.entity.CommonEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "images")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Image extends CommonEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String uri;

    @ManyToOne
    @JoinColumn(name = "post_id")
    @JsonBackReference
    private Post post;
}