package com.mctoluene.productinformationmanagement.domain.enums;

import java.util.Optional;

public enum ProductListing {
    MERCHLIST("MERCHLIST"), MERCHBUY("MERCHBUY"), AGENTAPP("AGENTAPP");

    ProductListing(String name) {
        this.name = name;
    }

    private final String name;

    public static Optional<ProductListing> getProductListing(String name) {

        for (ProductListing v : values())
            if (v.name.equalsIgnoreCase(name))
                return Optional.of(v);

        return Optional.empty();
    }
}
