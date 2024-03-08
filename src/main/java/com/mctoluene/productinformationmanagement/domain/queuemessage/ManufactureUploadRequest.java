package com.mctoluene.productinformationmanagement.domain.queuemessage;

import com.mctoluene.productinformationmanagement.util.PoiCell;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ManufactureUploadRequest {

    @PoiCell(name = "Manufacturer")
    private String manufacturerName;

    @PoiCell(name = "Description")
    private String description;

    private String createdBy;

}
