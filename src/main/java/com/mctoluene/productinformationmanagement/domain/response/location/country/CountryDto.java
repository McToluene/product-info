package com.mctoluene.productinformationmanagement.domain.response.location.country;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.io.Serializable;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
public record CountryDto(UUID publicId, String countryName, String twoLetterCode, String threeLetterCode,
        String dialingCode, String createdBy, String status) implements Serializable {
}
