package com.manna.fobe.matching.dto;

import com.manna.fobe.matching.entity.CustomMatching;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateCustomMatchingDto {
    private String meetingDate;
    private CustomMatching.AgePreference agePreference;
    private String location;
    private List<String> atmospheres;
}
