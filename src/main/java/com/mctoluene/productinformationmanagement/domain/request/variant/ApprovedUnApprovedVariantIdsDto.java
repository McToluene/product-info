package com.mctoluene.productinformationmanagement.domain.request.variant;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ApprovedUnApprovedVariantIdsDto {
    private List<String> validVariantsIds;

    private List<String> nonExistingSku;

    private Map<String, String> validSkuMap;

}
