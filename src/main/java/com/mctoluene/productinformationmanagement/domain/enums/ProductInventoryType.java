package com.mctoluene.productinformationmanagement.domain.enums;

import java.util.Optional;

public enum ProductInventoryType {

    LIVE_INVENTORY("LIVE_INVENTORY"), VIRTUAL_INVENTORY("VIRTUAL_INVENTORY");

    ProductInventoryType(String name) {
        this.name = name;
    }

    private final String name;

    public static Optional<ProductInventoryType> getProductInventory(String name) {

        for (ProductInventoryType v : values())
            if (v.name.equalsIgnoreCase(name))
                return Optional.of(v);

        return Optional.empty();
    }
}
