package com.mctoluene.productinformationmanagement.domain.response;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record BatchDetail(String batch, Integer total_quantity, Integer available_quantity, String mfg_date,
        String exp_date, Integer mrp, BigDecimal buy_price, BigDecimal cost_price) {
}
