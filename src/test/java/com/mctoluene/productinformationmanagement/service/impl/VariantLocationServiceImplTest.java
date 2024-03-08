package com.mctoluene.productinformationmanagement.service.impl;

import com.mctoluene.productinformationmanagement.domain.enums.Status;
import com.mctoluene.productinformationmanagement.domain.request.variantlocation.VariantLocationRequestdto;
import com.mctoluene.productinformationmanagement.domain.response.SearchResultDTO;
import com.mctoluene.productinformationmanagement.domain.response.VariantLocationResponseDto;
import com.mctoluene.productinformationmanagement.domain.response.productVariant.ProductVariantResponseDto;
import com.mctoluene.productinformationmanagement.model.ProductVariant;
import com.mctoluene.productinformationmanagement.model.VariantLocation;
import com.mctoluene.productinformationmanagement.service.MessageSourceService;
import com.mctoluene.productinformationmanagement.service.impl.VariantLocationInternalServiceImpl;
import com.mctoluene.productinformationmanagement.service.impl.VariantLocationServiceImpl;
import com.mctoluene.productinformationmanagement.service.internal.AlgoliaClientInternalService;
import com.mctoluene.productinformationmanagement.service.internal.LocationClientInternalService;
import com.mctoluene.productinformationmanagement.service.internal.VariantInternalService;
import com.mctoluene.commons.response.AppResponse;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class VariantLocationServiceImplTest {

    @Mock
    private VariantLocationInternalServiceImpl variantLocationInternalService;
    @Mock
    private LocationClientInternalService locationClientInternalService;
    @Mock
    private MessageSourceService messageSourceService;
    @InjectMocks
    private VariantLocationServiceImpl variantLocationService;
    @Mock
    private VariantInternalService variantInternalService;
    @Mock
    private AlgoliaClientInternalService algoliaClientInternalService;

    @Test
    void linkVariantToLocation() {
        VariantLocationRequestdto requestDto = VariantLocationRequestdto.builder()
                .statePublicId(UUID.randomUUID())
                .variantPublicId(UUID.randomUUID())
                .linkedBy("Kunal")
                .build();

        ProductVariant productVariant = ProductVariant.builder().build();
        productVariant.setPublicId(requestDto.getVariantPublicId());

        given(locationClientInternalService.getStateProvinceByPublicId(any())).willReturn(getLocationMock());
        given(variantLocationInternalService.checkIfProductLocationExist(any(), any()))
                .willReturn(getVariantLocationMock());
        given(variantInternalService.findProductVariantByPublicId(any())).willReturn(Optional.of(productVariant));
        given(variantLocationInternalService.saveProductToDb(any())).willReturn(getVariantLocation());

        var approveResponse = variantLocationService.linkVariantToLocation(requestDto);
        assertThat(approveResponse).isNotNull();
        assertThat(approveResponse.getMessage())
                .isEqualTo(messageSourceService.getMessageByKey("product.variant.location.link.created.successfully"));
        assertThat(approveResponse.getStatus()).isEqualTo(HttpStatus.CREATED.value());
    }

    @Test
    public void searchProductByQuery() {
        String query = "Milk";
        UUID locationPublicId = UUID.randomUUID();
        int page = 1;
        int size = 10;

        when(locationClientInternalService.getStateProvinceByPublicId(any(UUID.class))).thenReturn(getLocationMock());
        when(algoliaClientInternalService.searchProductByQuery(any(String.class), any(UUID.class), any(Integer.class),
                any(Integer.class))).thenReturn(getAlgoliaMock());

        var approveResponse = variantLocationService.searchProductByQuery(query, locationPublicId, page, size);
        assertThat(approveResponse).isNotNull();
        assertThat(approveResponse.getMessage())
                .isEqualTo("products retrieved successfully");
        assertThat(approveResponse.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    private boolean getVariantLocationMock() {
        return false;
    }

    private ResponseEntity<AppResponse> getLocationMock() {
        AppResponse appResponse = new AppResponse(HttpStatus.OK.value(), "", "",
                "State", null);
        return ResponseEntity.ok(appResponse);
    }

    private ResponseEntity<AppResponse> getVariantMock() {
        AppResponse appResponse = new AppResponse(HttpStatus.OK.value(), "", "",
                "Variant product", null);
        return ResponseEntity.ok(appResponse);
    }

    private VariantLocation getVariantLocation() {
        return VariantLocation.builder()
                .variantPublicId(UUID.randomUUID())
                .locationPublicId(UUID.randomUUID()).build();
    }

    private VariantLocationResponseDto convertToResponseDto() {
        return VariantLocationResponseDto.builder()
                .statePublicId(UUID.randomUUID())
                .variantPublicId(UUID.randomUUID())
                .status(Status.ACTIVE.name())
                .build();
    }

    private ResponseEntity<AppResponse> getAlgoliaMock() {
        SearchResultDTO searchResultDTO = new SearchResultDTO();
        List<ProductVariantResponseDto> productResponseDtoList = new ArrayList<ProductVariantResponseDto>();
        searchResultDTO.setHitsPerPage(10L);
        searchResultDTO.setNbPages(10L);
        searchResultDTO.setPage(1L);
        searchResultDTO.setNbHits(10L);
        for (int i = 0; i < 5; i++) {
            ProductVariantResponseDto productVariantResponseDto = ProductVariantResponseDto.builder().name("Milk" + i)
                    .publicId(UUID.randomUUID()).build();
            productResponseDtoList.add(productVariantResponseDto);
        }
        searchResultDTO.setHits(productResponseDtoList);
        AppResponse appResponse = new AppResponse(HttpStatus.OK.value(), "products retrieved successfully",
                "products retrieved successfully", searchResultDTO, null);
        return ResponseEntity.ok(appResponse);
    }
}
