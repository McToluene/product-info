package com.mctoluene.productinformationmanagement.service;

import java.util.UUID;

public interface TraceService {
    void propagateSleuthFields(UUID traceId);
}
