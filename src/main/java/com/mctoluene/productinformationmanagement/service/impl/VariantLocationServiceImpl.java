package com.mctoluene.productinformationmanagement.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mctoluene.productinformationmanagement.domain.request.variantlocation.VariantLocationRequestdto;
import com.mctoluene.productinformationmanagement.domain.response.SearchResultDTO;
import com.mctoluene.productinformationmanagement.domain.response.VariantLocationResponseDto;
import com.mctoluene.productinformationmanagement.domain.response.productVariant.ProductVariantResponseDto;
import com.mctoluene.productinformationmanagement.exception.UnProcessableEntityException;
import com.mctoluene.productinformationmanagement.exception.ValidatorException;
import com.mctoluene.productinformationmanagement.helper.VariantLocationHelper;
import com.mctoluene.productinformationmanagement.model.VariantLocation;
import com.mctoluene.productinformationmanagement.service.MessageSourceService;
import com.mctoluene.productinformationmanagement.service.VariantLocationService;
import com.mctoluene.productinformationmanagement.service.internal.AlgoliaClientInternalService;
import com.mctoluene.productinformationmanagement.service.internal.LocationClientInternalService;
import com.mctoluene.productinformationmanagement.service.internal.VariantInternalService;
import com.mctoluene.productinformationmanagement.service.internal.VariantLocationInternalService;
import com.mctoluene.commons.response.AppResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class VariantLocationServiceImpl implements VariantLocationService {

    private final VariantLocationInternalService variantLocationInternalService;
    private final LocationClientInternalService locationClientInternalService;
    private final MessageSourceService messageSourceService;
    private final VariantInternalService variantInternalService;
    private final AlgoliaClientInternalService algoliaClientInternalService;
    ObjectMapper mapper = new ObjectMapper();

    @Override
    public AppResponse linkVariantToLocation(VariantLocationRequestdto variantLocationRequestdto) {
        validateLocationByPublicId(variantLocationRequestdto.getStatePublicId());
        final var variant = variantInternalService
                .findProductVariantByPublicId(variantLocationRequestdto.getVariantPublicId());
        if (variant.isEmpty())
            throw new ValidatorException(messageSourceService.getMessageByKey("variant.not.found"));

        if (variantLocationInternalService.checkIfProductLocationExist(variantLocationRequestdto.getVariantPublicId(),
                variantLocationRequestdto.getStatePublicId()))
            throw new ValidatorException(
                    messageSourceService.getMessageByKey("product.variant.and.location.already.present"));
        VariantLocation variantLocation = VariantLocationHelper.buildVariantLocation(variantLocationRequestdto);
        variantLocation = variantLocationInternalService.saveProductToDb(variantLocation);
        VariantLocationResponseDto responseDto = VariantLocationHelper.buildResponseDto(variantLocation);
        return new AppResponse(HttpStatus.CREATED.value(),
                messageSourceService.getMessageByKey("product.variant.location.link.created.successfully"),
                messageSourceService.getMessageByKey("product.variant.location.link.created.successfully"), responseDto,
                null);
    }

    @Override
    public AppResponse searchProductByQuery(String query, UUID statePublicId, int page, int size) {
        page = (page <= 0) ? 0 : page - 1;
        size = (size <= 0) ? 0 : size;

        if (Objects.nonNull(statePublicId))
            validateLocationByPublicId(statePublicId);

        try {
            ResponseEntity<AppResponse> algoliaAppResponseEntity = algoliaClientInternalService
                    .searchProductByQuery(query, statePublicId, page, size);

            AppResponse algoliaAppResponse = algoliaAppResponseEntity.getBody();

            String jsonData = mapper.writeValueAsString(algoliaAppResponse.getData());
            SearchResultDTO searchResultDTO = mapper.readValue(jsonData, SearchResultDTO.class);

            Page<ProductVariantResponseDto> productResponseDtoPage = new PageImpl<>(searchResultDTO.getHits(),
                    PageRequest.of(page, size), searchResultDTO.getNbHits());

            log.info("Retrieved products by query {}", algoliaAppResponse.getData());
            return new AppResponse(algoliaAppResponse.getStatus(), algoliaAppResponse.getMessage(),
                    algoliaAppResponse.getSupportDescriptiveMessage(), productResponseDtoPage,
                    algoliaAppResponse.getError());

        } catch (Exception e) {
            throw new UnProcessableEntityException(
                    messageSourceService.getMessageByKey("could.not.process.request"));
        }
    }

    private void validateLocationByPublicId(UUID statePublicId) {
        try {
            var responseEntity = locationClientInternalService.getStateProvinceByPublicId(statePublicId);
            if (Objects.requireNonNull(responseEntity.getBody()).getData() == null
                    || responseEntity.getStatusCodeValue() != 200)
                throw new ValidatorException(messageSourceService.getMessageByKey("location.not.found"));
        } catch (Exception e) {
            log.info("Location with state-provinceId {} not found", statePublicId);
            throw new ValidatorException(messageSourceService.getMessageByKey("location.not.found"));
        }
    }
}
