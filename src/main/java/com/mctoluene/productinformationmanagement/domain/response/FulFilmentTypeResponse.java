package com.mctoluene.productinformationmanagement.domain.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FulFilmentTypeResponse {
    private String caption;
    private String value;
}
