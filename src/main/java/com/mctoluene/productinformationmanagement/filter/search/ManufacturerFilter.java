package com.mctoluene.productinformationmanagement.filter.search;

import lombok.*;
import org.apache.commons.lang3.StringUtils;

import com.mctoluene.productinformationmanagement.domain.enums.Status;
import com.mctoluene.productinformationmanagement.exception.ValidatorException;

import javax.persistence.criteria.Predicate;
import java.util.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ManufacturerFilter extends BaseFilter {

    private String filter;

    private String manufacturerName;

    private UUID publicId;

    private Status status;

    @Override
    public <T, D> void filter(QueryBuilder<T, D> queryBuilder) {
        super.filter(queryBuilder);

        if (!StringUtils.isEmpty(manufacturerName)) {
            queryBuilder.in("manufacturerName", Arrays.asList(manufacturerName.split(",")));
        }

        if (Objects.nonNull(publicId)) {
            queryBuilder.equal("publicId", publicId);
        }

        // validate only active and inactive status is allowed
        List<Status> validStatusList = Arrays.asList(Status.ACTIVE, Status.INACTIVE);

        if (Objects.isNull(status)) {
            queryBuilder.in("status", List.of(Status.ACTIVE));
        } else {
            String[] statusListFromQuery = status.toString().split(",");
            List<Status> statusInQuery = Arrays.stream(statusListFromQuery).map(s -> {
                Optional<Status> optionalStatus = Status.getStatus(s);
                if (optionalStatus.isEmpty())
                    throw new ValidatorException("Invalid status passed in query");
                return optionalStatus.get();
            }).toList();
            boolean doesNotContainInvalidStatus = statusInQuery.stream()
                    .filter(s -> !validStatusList.contains(s))
                    .toList().isEmpty();

            if (!doesNotContainInvalidStatus)
                throw new ValidatorException("Invalid status passed in query");

            queryBuilder.in("status", statusInQuery);
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

            List<String> stringFields = Arrays.asList("manufacturerName");

            // string query
            stringFields.forEach(field -> stringValues.forEach(value -> queryBuilder.like(field, "%" + value + "%")));

            Predicate searchPredicate = queryBuilder.getCriteriaBuilder().or(queryBuilder.getPredicates());

            queryBuilder.setPredicates(queryBuilder.getCriteriaBuilder().and(existingPredicate, searchPredicate));

        }

    }

}
