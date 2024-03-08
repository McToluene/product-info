package com.mctoluene.productinformationmanagement.domain.request.image;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ImageUploadRequestDto {

    private String imageName;

    private String imageUrl;
}
