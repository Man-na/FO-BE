package com.manna.fobe.marker.service;

import com.manna.fobe.marker.dto.CreateMarkerDto;
import com.manna.fobe.marker.entity.Image;
import com.manna.fobe.marker.entity.Marker;
import com.manna.fobe.marker.repository.MarkerRepository;
import com.manna.fobe.user.entity.User;
import com.manna.fobe.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MarkerServiceImpl implements MarkerService {

    private final MarkerRepository markerRepository;
    private final UserRepository userRepository;

    @Transactional
    @Override
    public Marker createMarker(CreateMarkerDto createMarkerDto, int userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Marker marker = Marker.builder()
                .title(createMarkerDto.getTitle())
                .address(createMarkerDto.getAddress())
                .date(createMarkerDto.getDate())
                .description(createMarkerDto.getDescription())
                .user(user)
                .latitude(createMarkerDto.getLatitude())
                .longitude(createMarkerDto.getLongitude())
                .color(createMarkerDto.getColor())
                .score(createMarkerDto.getScore())
                .build();

        if (createMarkerDto.getImageUris() != null) {
            List<Image> images = createMarkerDto.getImageUris().stream()
                    .map(img -> new Image(null, img.getUri(), marker))
                    .collect(Collectors.toList());
            marker.setImages(images);
        }

        return markerRepository.save(marker);
    }

    @Override
    public List<Marker> getMyMarkers(int userId) {
        return markerRepository.findByUserId(userId);
    }

    @Override
    public Marker getSingleMarker(int markerId) {
        return markerRepository.findById(markerId);
    }

    @Override
    public Page<Marker> getMyMarkers(int userId, Pageable pageable) {
        return markerRepository.findByUserId(userId, pageable);
    }

    @Override
    public Map<Integer, List<Marker>> getCalendarMarkers(int year, int month, int userId) {
        // 해당 연월의 시작일과 종료일 계산
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        // 해당 기간의 포스트 조회
        List<Marker> markers = markerRepository.findByUserIdAndDateBetween(userId, startDate, endDate);

        // 날짜별로 그룹화
        return markers.stream()
                .collect(Collectors.groupingBy(
                        marker -> marker.getDate().getDayOfMonth(),
                        Collectors.toList()
                ));
    }
}
