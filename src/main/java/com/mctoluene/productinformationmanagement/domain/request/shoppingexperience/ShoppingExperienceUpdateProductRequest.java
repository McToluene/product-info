package com.mctoluene.productinformationmanagement.domain.request.shoppingexperience;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ShoppingExperienceUpdateProductRequest {

    private String objectID;

    private UUID publicId;

    private String name;

    private String sku;

    private String categoryName;

    private List<String> imageUrls;

}
