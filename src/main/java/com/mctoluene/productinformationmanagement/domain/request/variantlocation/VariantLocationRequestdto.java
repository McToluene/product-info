package com.mctoluene.productinformationmanagement.domain.request.variantlocation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class VariantLocationRequestdto {
    @NotNull(message = "{empty.variant.public.id}")
    private UUID variantPublicId;
    @NotNull(message = "{empty.state.public.id}")
    private UUID statePublicId;
    @NotEmpty(message = "{empty.linked.by}")
    private String linkedBy;
}
