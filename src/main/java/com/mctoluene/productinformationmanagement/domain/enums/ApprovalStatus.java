package com.mctoluene.productinformationmanagement.domain.enums;

import java.util.Optional;

public enum ApprovalStatus {
    APPROVED("APPROVED"), PENDING("PENDING"), REJECTED("REJECTED");

    ApprovalStatus(String name) {
        this.name = name;
    }

    private final String name;

    public static Optional<ApprovalStatus> getApprovalStatus(String name) {

        for (ApprovalStatus v : values())
            if (v.name.equalsIgnoreCase(name))
                return Optional.of(v);

        return Optional.empty();
    }
}
