package com.mctoluene.productinformationmanagement.domain.enums;

import java.util.Optional;

public enum FileTypeExtension {
    XLSX("xlsx");

    FileTypeExtension(String name) {
        this.name = name;
    }

    private final String name;

    public static Optional<FileTypeExtension> getFileTypeExtension(String name) {

        for (FileTypeExtension v : values())
            if (v.name.equalsIgnoreCase(name))
                return Optional.of(v);

        return Optional.empty();
    }
}
