package com.fintech.transactionControl.Projections;

import java.math.BigDecimal;

public interface MonthlyProjection {
    Integer getYear();
    Integer getMonth();
    BigDecimal getTotal();
}
