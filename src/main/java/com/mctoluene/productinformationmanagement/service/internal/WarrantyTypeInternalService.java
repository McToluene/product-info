package com.mctoluene.productinformationmanagement.service.internal;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.mctoluene.productinformationmanagement.domain.request.warrantytype.UpdateWarrantyTypeRequestDto;
import com.mctoluene.productinformationmanagement.model.WarrantyType;

import java.util.UUID;

public interface WarrantyTypeInternalService {
    WarrantyType saveWarrantyTypeToDb(WarrantyType warrantyType);

    WarrantyType findByPublicId(UUID publicId);

    WarrantyType deleteWarrantyType(WarrantyType warrantyType);

    WarrantyType findWarrantyTypeByPublicId(UUID publicId);

    WarrantyType updateWarrantyType(WarrantyType warrantyType);

    WarrantyType findByPublicIdAndWarrantyType(UUID publicId, UpdateWarrantyTypeRequestDto requestDto);

    PageImpl<WarrantyType> getAllWarrantyType(Pageable pageable);
}
