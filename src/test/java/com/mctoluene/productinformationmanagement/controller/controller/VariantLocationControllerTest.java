package com.mctoluene.productinformationmanagement.controller.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mctoluene.productinformationmanagement.controller.VariantLocationController;
import com.mctoluene.productinformationmanagement.domain.request.variantlocation.VariantLocationRequestdto;
import com.mctoluene.productinformationmanagement.domain.response.productVariant.ProductVariantResponseDto;
import com.mctoluene.productinformationmanagement.filter.RequestHeaderContextHolder;
import com.mctoluene.productinformationmanagement.filter.dto.RequestHeaderContext;
import com.mctoluene.productinformationmanagement.service.LocationCacheInterfaceService;
import com.mctoluene.productinformationmanagement.service.MessageSourceService;
import com.mctoluene.productinformationmanagement.service.TraceService;
import com.mctoluene.productinformationmanagement.service.VariantLocationService;
import com.mctoluene.commons.response.AppResponse;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(VariantLocationController.class)
@Import(VariantLocationService.class)
public class VariantLocationControllerTest {

    @MockBean
    VariantLocationService variantLocationService;
    @Autowired
    MockMvc mockMvc;

    @MockBean
    public TraceService traceService;

    @MockBean
    public MessageSourceService messageSourceService;

    @MockBean
    public LocationCacheInterfaceService locationCacheInterfaceService;

    @Test
    void linkProductVariantToLocation() throws Exception {
        AppResponse responseEntity = new AppResponse(HttpStatus.OK.value(),
                "Product variant successfully linked to Location",
                "Product variant successfully linked to Location", null, null);

        VariantLocationRequestdto requestDto = VariantLocationRequestdto.builder()
                .statePublicId(UUID.randomUUID())
                .variantPublicId(UUID.randomUUID())
                .linkedBy("Kunal")
                .build();

        try (MockedStatic<RequestHeaderContextHolder> mockContext = Mockito
                .mockStatic(RequestHeaderContextHolder.class)) {
            mockContext.when(RequestHeaderContextHolder::getContext)
                    .thenReturn(new RequestHeaderContext(UUID.randomUUID().toString(), "NGN"));

            when(variantLocationService.linkVariantToLocation(any())).thenReturn(responseEntity);
            String inoutInJson = mapToJson(requestDto);
            mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/variant-location")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(inoutInJson)
                    .header("x-trace-id", UUID.randomUUID()))
                    .andDo(print())
                    .andExpect(status().isOk());
        }

    }

    @Test
    private Page<ProductVariantResponseDto> getAppResponse(PageRequest pageRequest, int count) {
        List<ProductVariantResponseDto> productResponseDtos = new ArrayList<>();
        for (int i = 0; i <= count; i++) {
            ProductVariantResponseDto productResponseDto = ProductVariantResponseDto.builder()
                    .publicId(UUID.randomUUID())
                    .build();
            productResponseDto.setPublicId(UUID.randomUUID());
            productResponseDto.setName("Milk");
            productResponseDto.setStatePublicId(UUID.randomUUID());
            productResponseDtos.add(productResponseDto);
        }
        return new PageImpl<>(productResponseDtos, pageRequest, productResponseDtos.size());
    }

    @Test
    void searchProductByQuery() throws Exception {
        AppResponse responseEntity = new AppResponse(HttpStatus.OK.value(),
                "Product fetched successfully",
                "Product fetched successfully", null, null);

        UUID stateId = UUID.randomUUID();

        try (MockedStatic<RequestHeaderContextHolder> mockContext = Mockito
                .mockStatic(RequestHeaderContextHolder.class)) {
            mockContext.when(RequestHeaderContextHolder::getContext)
                    .thenReturn(new RequestHeaderContext(UUID.randomUUID().toString(), "NGN"));

            when(variantLocationService.searchProductByQuery("search", stateId, 1, 10))
                    .thenReturn(responseEntity);
            doNothing().when(traceService).propagateSleuthFields(UUID.randomUUID());

            mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/variant-location/search/product")
                    .param("query", "search")
                    .param("statePublicId", stateId.toString())
                    .param("page", "1")
                    .param("size", "10")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header("x-trace-id", UUID.randomUUID()))
                    .andExpect(status().isOk());
        }

    }

    private String mapToJson(Object object) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(object);
    }

}
