package com.mctoluene.productinformationmanagement.domain.queuemessage;

import lombok.*;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductVariantQueueMessage implements Serializable {

    private List<ProductVariantUploadTemplateRequest> uploadTemplateRequestList;

    private UUID countryId;

    private String createdBy;

    private UUID productPublicId;

    private UUID traceId;

}
