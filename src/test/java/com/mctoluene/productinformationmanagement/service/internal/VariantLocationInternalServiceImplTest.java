package com.mctoluene.productinformationmanagement.service.internal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.mctoluene.productinformationmanagement.domain.enums.Status;
import com.mctoluene.productinformationmanagement.model.Manufacturer;
import com.mctoluene.productinformationmanagement.model.VariantLocation;
import com.mctoluene.productinformationmanagement.repository.VariantLocationRepository;
import com.mctoluene.productinformationmanagement.service.impl.VariantLocationInternalServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class VariantLocationInternalServiceImplTest {

    @Mock
    private VariantLocationRepository variantLocationRepository;

    @InjectMocks
    private VariantLocationInternalServiceImpl variantLocationInternalService;

    @Test
    void saveProductToDbTest() {
        UUID publicId = UUID.randomUUID();
        UUID locationId = UUID.randomUUID();
        UUID variantId = UUID.randomUUID();
        VariantLocation variantLocation = buildVariantLocation(variantId, locationId, publicId);
        when(variantLocationRepository.save(variantLocation)).thenReturn(variantLocation);

        var response = variantLocationInternalService.saveProductToDb(variantLocation);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(publicId);
    }

    @Test
    void checkIfProductLocationExistTest() {
        UUID variantId = UUID.randomUUID();
        UUID locationId = UUID.randomUUID();
        UUID publicId = UUID.randomUUID();

        VariantLocation variantLocation = buildVariantLocation(variantId, locationId, publicId);

        when(variantLocationRepository.findByVariantPublicIdAndLocationPublicIdAndStatus(variantId, locationId,
                Status.ACTIVE.toString()))
                .thenReturn(Optional.of(variantLocation));

        var response = variantLocationInternalService.checkIfProductLocationExist(variantId, locationId);

        assertThat(response).isNotNull();
        assertThat(response).isEqualTo(true);
    }

    @Test
    void findByVariantPublicIdTest() {
        UUID variantId = UUID.randomUUID();
        UUID locationId = UUID.randomUUID();
        UUID publicId = UUID.randomUUID();

        List<VariantLocation> variantLocations = new ArrayList<>();
        VariantLocation variantLocation = buildVariantLocation(variantId, locationId, publicId);
        variantLocations.add(variantLocation);

        when(variantLocationRepository.findAllByVariantPublicIdAndStatus(variantId, Status.ACTIVE.toString()))
                .thenReturn(variantLocations);

        var response = variantLocationInternalService.findByVariantPublicId(variantId);

        assertThat(response).isNotNull();
        assertThat(response.get(0).getVariantPublicId()).isEqualTo(variantId);
    }

    private VariantLocation buildVariantLocation(UUID variantId, UUID locationId, UUID publicId) {
        VariantLocation variantLocation = VariantLocation.builder()
                .locationPublicId(locationId)
                .variantPublicId(variantId)
                .build();
        variantLocation.setId(publicId);
        return variantLocation;
    }
}
