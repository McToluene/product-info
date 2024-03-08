package com.mctoluene.productinformationmanagement.domain.request.image;

public record FailedImageUploadDto(String reason, String imageName, String imageUrl) {
}
