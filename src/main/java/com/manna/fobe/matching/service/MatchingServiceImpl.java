package com.manna.fobe.matching.service;

import com.manna.fobe.matching.dto.CreateCustomMatchingDto;
import com.manna.fobe.matching.dto.CreateCustomMatchingResponseDto;
import com.manna.fobe.matching.dto.CreateRapidMatchingDto;
import com.manna.fobe.matching.dto.CreateRapidMatchingResponseDto;
import com.manna.fobe.matching.entity.Atmosphere;
import com.manna.fobe.matching.entity.CustomMatching;
import com.manna.fobe.matching.entity.MatchingAtmosphere;
import com.manna.fobe.matching.entity.RapidMatching;
import com.manna.fobe.matching.repository.AtmosphereRepository;
import com.manna.fobe.matching.repository.CustomMatchingRepository;
import com.manna.fobe.matching.repository.RapidMatchingRepository;
import com.manna.fobe.user.entity.User;
import com.manna.fobe.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MatchingServiceImpl implements MatchingService {

    private final UserRepository userRepository;
    private final RapidMatchingRepository rapidMatchingRepository;
    private final CustomMatchingRepository customMatchingRepository;
    private final AtmosphereRepository atmosphereRepository;

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

    @Override
    public CreateCustomMatchingResponseDto createCustomMatching(CreateCustomMatchingDto createCustomMatchingDto, int userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        CustomMatching customMatching = CustomMatching.builder()
                .user(user)
                .meetingDate(createCustomMatchingDto.getMeetingDate())
                .location(createCustomMatchingDto.getLocation())
                .agePreference(createCustomMatchingDto.getAgePreference())
                .status(CustomMatching.MatchingStatus.PENDING)
                .build();

        List<Atmosphere> atmospheres = createCustomMatchingDto.getAtmospheres().stream()
                .map(atmosphereId -> atmosphereRepository.findByAtmosphereId(Integer.parseInt(atmosphereId))
                        .orElseThrow(() -> new IllegalArgumentException("Atmosphere not found: " + atmosphereId)))
                .toList();

        List<MatchingAtmosphere> matchingAtmospheres = atmospheres.stream()
                .map(atmosphere -> MatchingAtmosphere.builder()
                        .customMatching(customMatching)
                        .atmosphere(atmosphere)
                        .build())
                .collect(Collectors.toList());

        customMatching.setAtmospheres(matchingAtmospheres);

        CustomMatching savedMatching = customMatchingRepository.save(customMatching);

        return CreateCustomMatchingResponseDto.builder()
                .matchingId(savedMatching.getMatchingId())
                .userId(savedMatching.getUser().getId())
                .meetingDate(savedMatching.getMeetingDate())
                .location(savedMatching.getLocation())
                .agePreference(savedMatching.getAgePreference())
                .status(savedMatching.getStatus())
                .build();
    }
}