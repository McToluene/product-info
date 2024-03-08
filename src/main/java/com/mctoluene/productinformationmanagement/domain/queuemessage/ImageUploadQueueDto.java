package com.mctoluene.productinformationmanagement.domain.queuemessage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import com.mctoluene.productinformationmanagement.domain.request.image.ImageUploadRequestDto;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ImageUploadQueueDto {
    private List<ImageUploadRequestDto> imageUploadRequestDtos;
    private String uploadedBy;
}
