package com.example.demo.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.demo.dto.request.CreateLineRequest;
import com.example.demo.dto.request.UpdateLineRequest;
import com.example.demo.dto.response.LineResponse;
import com.example.demo.dto.response.PageResponse;
import com.example.demo.dto.response.ProductionOrderResponse;
import com.example.demo.entity.Line;
import com.example.demo.mapper.LineMapper;
import com.example.demo.repository.LineRepository;
import com.example.demo.service.interf.LineService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class LineServiceImpl implements LineService {

    private final LineRepository lineRepository;
    private final LineMapper lineMapper;

    @Override
    public LineResponse getLineById(Long lineId) {
        // Implement the logic to retrieve a line by its ID

        Line line = lineRepository.findByIdAndIsDeletedFalse(lineId)
                .orElseThrow(() -> new RuntimeException("Line not found with ID: " + lineId));

        return lineMapper.toResponse(line);
    }

    @Override
    public LineResponse createLine(CreateLineRequest lineResponse) {
        // Implement the logic to create a new line

        Line line= lineMapper.toEntity(lineResponse);
        lineRepository.save(line);

        return lineMapper.toResponse(line);
    }

    @Override
    public LineResponse updateLine(Long lineId, UpdateLineRequest lineResponse) {
        // Implement the logic to update an existing line

        Line existingLine = lineRepository.findByIdAndIsDeletedFalse(lineId)
                .orElseThrow(() -> new RuntimeException("Line not found with ID: " + lineId));

        lineMapper.updateEntityFromRequest(lineResponse, existingLine);

        lineRepository.save(existingLine);

        return lineMapper.toResponse(existingLine);
    }

    @Override
    public List<ProductionOrderResponse> getPObyLineId(Long lineId) {
        // Implement the logic to retrieve production orders by line ID

        

        return null;
    }

    @Override
    public PageResponse<LineResponse> getAllLines(Pageable pageable, Optional<String> lineCode, Optional<String> name) {
        // Implement the logic to retrieve all lines with pagination and optional filtering by lineCode and name

        Page<Line> lines = lineRepository.findAllByIsDeletedFalse(pageable, lineCode, name);

        return PageResponse.fromPage(lines.map(lineMapper::toResponse));
    }

    @Override
    public List<LineResponse> getAllLinesWithoutPagination() {
        List<Line> lines = lineRepository.findAllByIsDeletedFalse();
        return lineMapper.toResponseList(lines);
    }

}
