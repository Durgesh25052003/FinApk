package com.fintech.transactionControl.Projections;

import java.math.BigDecimal;

public interface CategoryProjection {
    String getCategory();
    BigDecimal getTotal();
}
