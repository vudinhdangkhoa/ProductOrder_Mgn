package com.example.demo.service.interf;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;

import com.example.demo.dto.request.CreateLineRequest;
import com.example.demo.dto.request.UpdateLineRequest;
import com.example.demo.dto.response.LineResponse;
import com.example.demo.dto.response.PageResponse;
import com.example.demo.dto.response.ProductionOrderResponse;

public interface LineService {

    PageResponse<ProductionOrderResponse> getPObyLineId(Long lineId, Pageable pageable);

    LineResponse getLineById(Long lineId);

    LineResponse createLine(CreateLineRequest lineResponse);

    LineResponse updateLine(Long lineId, UpdateLineRequest lineResponse);

    PageResponse<LineResponse> getAllLines(Pageable pageable, Optional<String> lineCode, Optional<String> name);

    List<LineResponse> getAllLinesWithoutPagination();
}
