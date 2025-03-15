package com.manna.fobe.matching.service;

import com.manna.fobe.matching.dto.CreateRapidMatchingDto;
import com.manna.fobe.matching.dto.CreateRapidMatchingResponseDto;

public interface MatchingService {
    CreateRapidMatchingResponseDto createRapidMatching(CreateRapidMatchingDto createRapidMatchingDto, int userId);
}
