package com.manna.fobe.marker.controller;

import com.manna.fobe.common.dto.ResponseMessage;
import com.manna.fobe.marker.dto.CreateMarkerDto;
import com.manna.fobe.marker.entity.Marker;
import com.manna.fobe.marker.service.MarkerService;
import com.manna.fobe.common.utils.S3Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/marker")
@RequiredArgsConstructor
public class MarkerController {

    private final MarkerService markerService;
    private final S3Utils s3Utils;

    // marker 추가
    @PostMapping()
    public ResponseEntity<ResponseMessage> createMarker(@RequestBody CreateMarkerDto createMarkerDto, @RequestAttribute("userId") int userId) {
        Marker createdMarker = markerService.createMarker(createMarkerDto, userId);

        ResponseMessage response = ResponseMessage.builder()
                .data(createdMarker)
                .statusCode(201)
                .resultMessage("마커 추가 성공")
                .build();

        return ResponseEntity.status(201).body(response);
    }

    // 마커 전체 조회
    @GetMapping("/markers/all")
    public ResponseEntity<ResponseMessage> getMarkers(
    ) {
        List<Marker> markers = markerService.getMarkers();

        ResponseMessage responseMessage = ResponseMessage.builder()
                .data(markers)
                .statusCode(200)
                .resultMessage("마커 조회 성공")
                .build();

        return ResponseEntity.ok(responseMessage);
    }


    // 개별 마커 조회
    @GetMapping("/{id}")
    public ResponseEntity<ResponseMessage> getSingleMarker(@PathVariable("id") int id) {

        Marker marker = markerService.getSingleMarker(id);

        ResponseMessage responseMessage = ResponseMessage.builder()
                .data(marker)
                .statusCode(200)
                .resultMessage("마커 조회 성공")
                .build();

        return ResponseEntity.ok(responseMessage);
    }

    @GetMapping("/calendar")
    public ResponseEntity<ResponseMessage> getCalendarMarkers(
            @RequestParam("year") int year,
            @RequestParam("month") int month,
            @RequestAttribute("userId") int userId
    ) {
        Map<Integer, List<Marker>> calendarPosts = markerService.getCalendarMarkers(year, month, userId);

        ResponseMessage responseMessage = ResponseMessage.builder()
                .data(calendarPosts)
                .statusCode(200)
                .resultMessage("캘린더 포스트 조회 성공")
                .build();

        return ResponseEntity.ok(responseMessage);
    }

    // 이미지 업로드
    @PostMapping("/images")
    public ResponseEntity<ResponseMessage> uploadImages(@RequestParam("images") List<MultipartFile> files) throws IOException {
        List<String> imagePaths = new ArrayList<>();

        for (MultipartFile file : files) {
            imagePaths.add(s3Utils.uploadFile(file));
        }

        ResponseMessage responseMessage = ResponseMessage.builder()
                .data(imagePaths)
                .statusCode(200)
                .resultMessage("이미지 업로드 성공")
                .build();

        return ResponseEntity.ok(responseMessage);
    }
}
