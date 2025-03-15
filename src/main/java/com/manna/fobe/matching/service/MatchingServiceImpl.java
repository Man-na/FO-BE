package com.manna.fobe.matching.service;

import com.manna.fobe.matching.dto.CreateRapidMatchingDto;
import com.manna.fobe.matching.dto.CreateRapidMatchingResponseDto;
import com.manna.fobe.matching.entity.RapidMatching;
import com.manna.fobe.matching.repository.RapidMatchingRepository;
import com.manna.fobe.user.entity.User;
import com.manna.fobe.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MatchingServiceImpl implements MatchingService {

    private final RapidMatchingRepository rapidMatchingRepository;
    private final UserRepository userRepository;

    @Override
    public CreateRapidMatchingResponseDto createRapidMatching(CreateRapidMatchingDto createRapidMatchingDto, int userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        RapidMatching rapidMatching = RapidMatching.builder()
                .user(user)
                .priority1Day(createRapidMatchingDto.getPriority1Day())
                .priority2Day(createRapidMatchingDto.getPriority2Day())
                .agePreference(createRapidMatchingDto.getAgePreference())
                .status(RapidMatching.MatchingStatus.PENDING)
                .build();

        RapidMatching savedMatching = rapidMatchingRepository.save(rapidMatching);

        return CreateRapidMatchingResponseDto.builder()
                .matchingId(savedMatching.getMatchingId())
                .userId(savedMatching.getUser().getId())
                .priority1Day(savedMatching.getPriority1Day())
                .priority2Day(savedMatching.getPriority2Day())
                .agePreference(savedMatching.getAgePreference())
                .status(savedMatching.getStatus())
                .build();
    }
}