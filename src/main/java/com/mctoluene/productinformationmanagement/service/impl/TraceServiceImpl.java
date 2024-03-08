package com.mctoluene.productinformationmanagement.service.impl;

import brave.baggage.BaggageField;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.mctoluene.productinformationmanagement.service.TraceService;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class TraceServiceImpl implements TraceService {
    private final BaggageField trackingCodeTraceField;

    public void propagateSleuthFields(UUID traceId) {
        trackingCodeTraceField.updateValue(traceId.toString());
    }

}
