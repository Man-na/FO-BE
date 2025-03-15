package com.manna.fobe.matching.controller;

import com.manna.fobe.common.dto.ResponseMessage;
import com.manna.fobe.matching.dto.CreateCustomMatchingDto;
import com.manna.fobe.matching.dto.CreateCustomMatchingResponseDto;
import com.manna.fobe.matching.dto.CreateRapidMatchingDto;
import com.manna.fobe.matching.dto.CreateRapidMatchingResponseDto;
import com.manna.fobe.matching.entity.RapidMatching;
import com.manna.fobe.matching.service.MatchingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/matching")
@RequiredArgsConstructor
public class MatchingController {

    private final MatchingService matchingService;

    @PostMapping("/rapid-matching")
    public ResponseEntity<ResponseMessage> createRapidMatching(
            @RequestBody CreateRapidMatchingDto createRapidMatchingDto,
            @RequestAttribute("userId") int userId) {
        CreateRapidMatchingResponseDto createdRapidMatching = matchingService.createRapidMatching(createRapidMatchingDto, userId);

        ResponseMessage response = ResponseMessage.builder()
                .data(createdRapidMatching)
                .statusCode(201)
                .resultMessage("빠른 매칭 생성 성공")
                .build();

        return ResponseEntity.status(201).body(response);
    }

    @PostMapping("/custom-matching")
    public ResponseEntity<ResponseMessage> createCustomMatching(
            @RequestBody CreateCustomMatchingDto createcustomMatchingDto,
            @RequestAttribute("userId") int userId) {
        CreateCustomMatchingResponseDto createdCustomMatching = matchingService.createCustomMatching(createcustomMatchingDto, userId);

        ResponseMessage response = ResponseMessage.builder()
                .data(createdCustomMatching)
                .statusCode(201)
                .resultMessage("직접 매칭 생성 성공")
                .build();

        return ResponseEntity.status(201).body(response);
    }

}