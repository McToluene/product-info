package com.mctoluene.productinformationmanagement.domain.queuemessage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BulkProductUploadRequest implements Serializable {

    private List<ImageUploadTemplateRequest> imageUploadTemplateRequests;

    private List<PriceTemplateRequest> priceTemplateRequests;

    private List<StockUpdateTemplateRequest> stockUpdateTemplateRequests;

    private Map<String, List<CategoryUploadTemplateRequest>> categoryUploadTemplateRequests;

    @NotNull(message = "trace id must be provided")
    private UUID traceId;

    @NotEmpty(message = "{empty.created.by}")
    private String createdBy;
    @NotNull(message = "country id must be provided")
    private UUID countryId;
}
