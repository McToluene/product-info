package com.mctoluene.productinformationmanagement.domain.eventDto;

import com.mctoluene.productinformationmanagement.domain.enums.TypeMessage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageContentEvent<T> {
    private String typeMessage;
    private T data;
}
