package com.mctoluene.productinformationmanagement.domain.request.location;

import lombok.Getter;
import lombok.Setter;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class IdListDto {
    private List<UUID> ids;
}
