package com.mctoluene.productinformationmanagement.filter.search;

import lombok.*;
import org.apache.commons.lang3.StringUtils;

import com.mctoluene.productinformationmanagement.domain.enums.Status;
import com.mctoluene.productinformationmanagement.exception.ValidatorException;
import com.mctoluene.productinformationmanagement.model.*;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDate;
import java.util.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductFilter extends BaseFilter {

    private String filter;

    private String productName;

    private UUID publicId;

    private String status;

    private UUID brandPublicId;
    private UUID manufacturerPublicId;

    private UUID categoryPublicId;
    private UUID warrantyTypePublicId;

    private UUID measuringUnitPublicId;

    private boolean vated;

    @Override
    public <T, D> void filter(QueryBuilder<T, D> queryBuilder) {
        super.filter(queryBuilder);

        if (!StringUtils.isEmpty(productName)) {
            queryBuilder.in("productName", Arrays.asList(productName.split(",")));
        }

        if (Objects.nonNull(publicId)) {
            queryBuilder.equal("publicId", publicId);
        }

        if (!Objects.isNull(brandPublicId)) {
            Root<T> root = queryBuilder.getRoot();
            Join<Brand, Product> brandJoin = root.join("brand", JoinType.INNER);
            queryBuilder.equal(brandJoin.get("publicId"), brandPublicId);

        }

        if (!Objects.isNull(categoryPublicId)) {
            Root<T> root = queryBuilder.getRoot();
            Join<ProductCategory, Product> categoryProductJoin = root.join("productCategory", JoinType.INNER);
            queryBuilder.equal(categoryProductJoin.get("publicId"), categoryPublicId);

        }

        if (!Objects.isNull(manufacturerPublicId)) {
            Root<T> root = queryBuilder.getRoot();
            Join<Manufacturer, Product> manufacturerProductJoin = root.join("manufacturer", JoinType.INNER);
            queryBuilder.equal(manufacturerProductJoin.get("publicId"), manufacturerPublicId);

        }

        if (!Objects.isNull(warrantyTypePublicId)) {
            Root<T> root = queryBuilder.getRoot();
            Join<WarrantyType, Product> warrantyTypeProductJoin = root.join("warrantyType", JoinType.INNER);
            queryBuilder.equal(warrantyTypeProductJoin.get("publicId"), warrantyTypePublicId);

        }

        if (!Objects.isNull(measuringUnitPublicId)) {
            Root<T> root = queryBuilder.getRoot();
            Join<MeasuringUnit, Product> measuringUnitProductJoin = root.join("measuringUnit", JoinType.INNER);
            queryBuilder.equal(measuringUnitProductJoin.get("publicId"), measuringUnitPublicId);

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

            List<String> stringFields = Arrays.asList("productName");

            // string query
            stringFields.forEach(field -> stringValues.forEach(value -> queryBuilder.like(field, "%" + value + "%")));

            Predicate searchPredicate = queryBuilder.getCriteriaBuilder().or(queryBuilder.getPredicates());

            queryBuilder.setPredicates(queryBuilder.getCriteriaBuilder().and(existingPredicate, searchPredicate));

        }
    }
}
