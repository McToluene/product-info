package com.mctoluene.productinformationmanagement.domain.request.variant;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.UUID;

@Builder
@Data
public class VariantFilterRequestDto {

    @NotEmpty(message = "variant PublicIdList name cannot be empty")
    private List<UUID> variantPublicIds;

    private String status;

    private String searchValue;

    private List<UUID> categoryPublicIds;

}