package com.manna.fobe.marker.service;

import com.manna.fobe.marker.dto.CreateMarkerDto;
import com.manna.fobe.marker.entity.Marker;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface MarkerService {
    Marker createMarker(CreateMarkerDto createMarkerDto, int userId);

    List<Marker> getMarkers();

    Marker getSingleMarker(int markerId);

    Map<Integer, List<Marker>> getCalendarMarkers(int year, int month, int userId);
}