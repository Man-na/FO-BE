package com.manna.fobe.matching.entity;

import com.manna.fobe.common.entity.CommonEntity;
import com.manna.fobe.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "custom_matching")
@Data
@EqualsAndHashCode(callSuper = false)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomMatching extends CommonEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int matchingId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String meetingDate;

    @Column(nullable = false)
    private String location;

    private String agePreference;

    @Enumerated(EnumType.STRING)
    private MatchingStatus status = MatchingStatus.PENDING;

    @OneToMany(mappedBy = "customMatching", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MatchingAtmosphere> atmospheres = new ArrayList<>();

    public enum MatchingStatus {
        PENDING, MATCHED, CANCELED
    }
}