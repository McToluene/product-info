package com.mctoluene.productinformationmanagement.domain.request.image;

import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;

@Builder
@Data
public class BulkUploadImageRequestDto {

    private MultipartFile[] image;

    @NotBlank(message = "{image.created.by.blank}")
    private String createdBy;
}
