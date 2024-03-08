package com.mctoluene.productinformationmanagement.domain.queuemessage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

import com.mctoluene.productinformationmanagement.util.PoiCell;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BrandUploadRequest {

    @PoiCell(name = "Brand")
    private String brandName;

    @PoiCell(name = "Description")
    private String description;

    private String createdBy;
    private UUID manufacturerId;

}
