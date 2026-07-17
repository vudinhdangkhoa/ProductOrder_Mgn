package com.example.demo.service.interf;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.demo.dto.request.UpdateLineRequest;
import com.example.demo.dto.response.LineResponse;
import com.example.demo.dto.response.ProductionOrderResponse;
import com.example.demo.dto.request.CreateLineRequest;

public interface LineService {

    List<ProductionOrderResponse> getPObyLineId(Long lineId);

    LineResponse getLineById(Long lineId);

    LineResponse createLine(CreateLineRequest lineResponse);

    LineResponse updateLine(Long lineId, UpdateLineRequest lineResponse);

    Page<LineResponse> getAllLines(Pageable pageable, Optional<String> lineCode, Optional<String> name);

}
