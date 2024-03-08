package com.mctoluene.productinformationmanagement.service.impl;

import brave.baggage.BaggageField;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.mctoluene.productinformationmanagement.service.impl.TraceServiceImpl;

import java.util.UUID;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class TraceServiceTest {

    @Mock
    private BaggageField trackingCodeTraceField;

    @InjectMocks
    private TraceServiceImpl traceService;

    @Test
    void propagateSleuthFieldsTest() {
        UUID traceId = UUID.randomUUID();
        when(trackingCodeTraceField.updateValue(traceId.toString())).thenReturn(true);
        traceService.propagateSleuthFields(traceId);
    }

}
