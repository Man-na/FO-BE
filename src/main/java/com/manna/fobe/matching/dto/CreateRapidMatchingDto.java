package com.manna.fobe.matching.dto;

import com.manna.fobe.common.entity.CommonEntity;
import com.manna.fobe.matching.entity.RapidMatching;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.annotations.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateRapidMatchingDto extends CommonEntity {

    private String priority1Day;

    private String priority2Day;

    @NotNull
    private RapidMatching.AgePreference agePreference;

}
