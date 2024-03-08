package com.mctoluene.productinformationmanagement.filter.search;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import com.mctoluene.productinformationmanagement.domain.constants.CommonConstants;
import com.mctoluene.productinformationmanagement.exception.ValidatorException;

import java.time.*;
import java.time.format.DateTimeFormatter;

@Slf4j
@Getter
@Setter
public abstract class BaseFilter {
    private String from;
    private String to;

    public BaseFilter() {

    }

    public <T, D> void filter(final QueryBuilder<T, D> queryBuilder) {
        if (!StringUtils.isEmpty(from) && !StringUtils.isEmpty(to)) {
            final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(CommonConstants.Pattern.DATE_FORMAT);

            try {
                final LocalDateTime fromDate = LocalDate.parse(from, formatter).atStartOfDay();
                final LocalDateTime toDate = LocalDate.parse(to, formatter).atTime(LocalTime.MAX);

                queryBuilder.between("createdDate", fromDate, toDate);
            } catch (Exception e) {
                log.info("date range error");
                throw new ValidatorException("date range error");
            }

        }
    }

}
