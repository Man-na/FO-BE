package com.manna.fobe.matching.dto;

import com.manna.fobe.matching.entity.CustomMatching;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCustomMatchingResponseDto {
    private int matchingId;
    private int userId;
    private String meetingDate;
    private String location;
    private CustomMatching.AgePreference agePreference;
    private CustomMatching.MatchingStatus status;
}