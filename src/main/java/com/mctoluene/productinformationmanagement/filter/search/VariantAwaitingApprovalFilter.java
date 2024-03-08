package com.mctoluene.productinformationmanagement.filter.search;

import lombok.*;
import org.apache.commons.lang3.StringUtils;

import com.mctoluene.productinformationmanagement.domain.enums.Status;
import com.mctoluene.productinformationmanagement.exception.ValidatorException;
import com.mctoluene.productinformationmanagement.model.Product;
import com.mctoluene.productinformationmanagement.model.ProductVariant;
import com.mctoluene.productinformationmanagement.model.VariantType;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VariantAwaitingApprovalFilter extends BaseFilter {

    private String filter;

    private String productVariantName;

    private String sku;

    private UUID publicId;

    private UUID productPublicId;

    private UUID variantTypePublicId;

    private String status;

    @Override
    public <T, D> void filter(QueryBuilder<T, D> queryBuilder) {
        super.filter(queryBuilder);

        if (!StringUtils.isEmpty(productVariantName)) {
            queryBuilder.in("variantName", Arrays.asList(productVariantName.split(",")));
        }

        if (Objects.nonNull(publicId)) {
            queryBuilder.equal("publicId", publicId);
        }

        if (Objects.nonNull(sku)) {
            queryBuilder.equal("sku", sku);
        }

        if (!Objects.isNull(productPublicId)) {
            Root<T> root = queryBuilder.getRoot();
            Join<Product, ProductVariant> productVariantJoin = root.join("product", JoinType.INNER);
            queryBuilder.equal(productVariantJoin.get("publicId"), productPublicId);

        }

        if (!Objects.isNull(variantTypePublicId)) {
            Root<T> root = queryBuilder.getRoot();
            Join<VariantType, ProductVariant> variantTypeJoin = root.join("variantType", JoinType.INNER);
            queryBuilder.equal(variantTypeJoin.get("publicId"), variantTypePublicId);

        }

        // validate only active and inactive status is allowed
        List<String> validStatusList = Arrays.asList(Status.ACTIVE.name(), Status.INACTIVE.name());

        if (Objects.isNull(status)) {
            queryBuilder.in("status", List.of(Status.ACTIVE.name()));
        } else {
            String[] statusListFromQuery = status.toString().split(",");
            List<Status> statusInQuery = Arrays.stream(statusListFromQuery).map(s -> {
                Optional<Status> optionalStatus = Status.getStatus(s);
                if (optionalStatus.isEmpty())
                    throw new ValidatorException("Invalid status passed in query");
                return optionalStatus.get();
            }).toList();
            boolean doesNotContainInvalidStatus = statusInQuery.stream()
                    .filter(s -> !validStatusList.contains(s.toString()))
                    .toList().isEmpty();

            if (!doesNotContainInvalidStatus)
                throw new ValidatorException("Invalid status passed in query");

            queryBuilder.in("status", Arrays.asList(statusListFromQuery));
        }

        filterByString(queryBuilder);

    }

    private <T, D> void filterByString(QueryBuilder<T, D> queryBuilder) {
        if (filter != null) {
            final String[] filterables = filter.split("%20|\\s+");

            List<String> stringValues = new ArrayList<>();

            for (String value : filterables) {
                stringValues.add(value);
            }

            Predicate existingPredicate = queryBuilder.getCriteriaBuilder().and(queryBuilder.getPredicates());
            queryBuilder.setPredicates(); // reset predicates

            List<String> stringFields = Arrays.asList("variantName", "sku");

            // string query
            stringFields.forEach(field -> stringValues.forEach(value -> queryBuilder.like(field, "%" + value + "%")));

            Predicate searchPredicate = queryBuilder.getCriteriaBuilder().or(queryBuilder.getPredicates());

            queryBuilder.setPredicates(queryBuilder.getCriteriaBuilder().and(existingPredicate, searchPredicate));

        }

    }
}
