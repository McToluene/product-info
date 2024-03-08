package com.mctoluene.productinformationmanagement.domain.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mctoluene.productinformationmanagement.domain.response.productVariant.ProductVariantResponseDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchResultDTO {
    private Long page;
    private Long nbHits;
    private Long nbPages;
    private Long hitsPerPage;
    private List<ProductVariantResponseDto> hits;

}
