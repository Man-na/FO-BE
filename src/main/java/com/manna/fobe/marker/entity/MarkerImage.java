package com.manna.fobe.marker.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.manna.fobe.common.entity.CommonEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "marker_images")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MarkerImage extends CommonEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String uri;

    @ManyToOne
    @JoinColumn(name = "marker_id")
    @JsonBackReference
    private Marker marker;
}