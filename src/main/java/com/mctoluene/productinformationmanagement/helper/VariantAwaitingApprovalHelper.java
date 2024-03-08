package com.mctoluene.productinformationmanagement.helper;

import org.apache.commons.text.WordUtils;

import com.mctoluene.productinformationmanagement.domain.enums.ApprovalStatus;
import com.mctoluene.productinformationmanagement.domain.enums.Status;
import com.mctoluene.productinformationmanagement.domain.request.variant.UpdateVariantRequestDto;
import com.mctoluene.productinformationmanagement.model.ProductVariant;
import com.mctoluene.productinformationmanagement.model.VariantAwaitingApproval;
import com.mctoluene.productinformationmanagement.model.VariantType;
import com.mctoluene.productinformationmanagement.util.StringSanitizerUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.UUID;

public class VariantAwaitingApprovalHelper {

    private VariantAwaitingApprovalHelper() {
    }

    public static VariantAwaitingApproval buildVariantAwaitingApproval(UpdateVariantRequestDto requestDto,
            VariantType variantType, ProductVariant oldVariant) {
        VariantAwaitingApproval variantAwaitingApproval = new VariantAwaitingApproval();
        variantAwaitingApproval.setVariantName(
                WordUtils.capitalizeFully(StringSanitizerUtils.sanitizeInput(requestDto.getVariantName().trim())));
        variantAwaitingApproval.setVariantDescription(requestDto.getVariantDescription());
        variantAwaitingApproval.setVariantType(variantType);
        variantAwaitingApproval.setStatus(Status.ACTIVE.name());
        variantAwaitingApproval.setCostPrice(requestDto.getCostPrice());
        variantAwaitingApproval.setApprovalStatus(ApprovalStatus.PENDING.name());
        variantAwaitingApproval.setPublicId(UUID.randomUUID());
        variantAwaitingApproval.setCreatedDate(LocalDateTime.now());
        variantAwaitingApproval.setCreatedBy(requestDto.getModifiedBy());
        variantAwaitingApproval.setProductVariant(oldVariant);
        variantAwaitingApproval.setProduct(oldVariant.getProduct());
        variantAwaitingApproval.setCreatedBy(requestDto.getModifiedBy());
        variantAwaitingApproval.setCreatedDate(LocalDateTime.now());
        variantAwaitingApproval.setLastModifiedBy(requestDto.getModifiedBy());
        variantAwaitingApproval.setLastModifiedDate(LocalDateTime.now());
        variantAwaitingApproval.setCountryId(requestDto.getCountryPublicId());
        variantAwaitingApproval.setVatValue(requestDto.getVatValue());
        return variantAwaitingApproval;
    }

    public static VariantAwaitingApproval buildVariantAwaitingApproval(VariantType variantType, String variantName,
            BigDecimal costPrice, String createdBy,
            String variantDetailsJsonString, UUID countryId,
            Double weight) {
        VariantAwaitingApproval variantAwaitingApproval = new VariantAwaitingApproval();
        variantAwaitingApproval.setPublicId(UUID.randomUUID());
        variantAwaitingApproval
                .setVariantName(WordUtils.capitalizeFully(StringSanitizerUtils.sanitizeInput(variantName.trim())));
        variantAwaitingApproval.setVariantType(variantType);
        variantAwaitingApproval.setCostPrice(costPrice);
        variantAwaitingApproval.setCreatedDate(LocalDateTime.now());
        variantAwaitingApproval.setStatus(Status.ACTIVE.name());
        variantAwaitingApproval.setCreatedBy(createdBy);
        variantAwaitingApproval.setProductVariantDetails(variantDetailsJsonString);
        variantAwaitingApproval.setApprovalStatus(ApprovalStatus.PENDING.name());
        variantAwaitingApproval.setLastModifiedDate(LocalDateTime.now());
        variantAwaitingApproval.setVersion(BigInteger.ZERO);
        variantAwaitingApproval.setCountryId(countryId);
        variantAwaitingApproval.setWeight(weight);
        return variantAwaitingApproval;
    }
}
