package com.manna.fobe.matching.dto;

import com.manna.fobe.matching.entity.RapidMatching;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateRapidMatchingResponseDto {
    private int matchingId;
    private int userId;
    private String priority1Day;
    private String priority2Day;
    private String agePreference;
    private RapidMatching.MatchingStatus status;
}