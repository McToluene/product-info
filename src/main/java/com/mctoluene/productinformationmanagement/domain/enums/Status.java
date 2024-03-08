package com.mctoluene.productinformationmanagement.domain.enums;

import java.util.Optional;

public enum Status {
    ACTIVE("ACTIVE"), INACTIVE("INACTIVE"), DELETED("DELETED");

    Status(String name) {
        this.name = name;
    }

    private final String name;

    public static Optional<Status> getStatus(String name) {

        for (Status v : values())
            if (v.name.equalsIgnoreCase(name))
                return Optional.of(v);

        return Optional.empty();
    }
}
