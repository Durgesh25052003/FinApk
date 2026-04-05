package com.fintech.transactionControl.Repo;

import com.fintech.transactionControl.Projections.CategoryProjection;
import com.fintech.transactionControl.Projections.MonthlyProjection;
import com.fintech.transactionControl.entities.FinancialRecord;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;

public interface FinRecRepo extends JpaRepository <FinancialRecord,Long>, JpaSpecificationExecutor<FinancialRecord> {
    List<FinancialRecord> findByUser_Id(Long userId, Pageable page);

    @Query("""
            SELECT COALESCE(SUM(f.amount))
            FROM FinancialRecord f
            WHERE f.user.id =:userId AND f.type = 'EXPENSE'
            """)
    BigDecimal getTotalExpense(Long userId);

    @Query("""
           SELECT COALESCE(SUM(f.amount))
           FROM FinancialRecord f
           WHERE f.user.id =:userId AND f.type = 'INCOME'
           """)
    BigDecimal getTotalIncome(Long userId);

    @Query("""
            SELECT SUM(f.amount) as total , f.category as category FROM FinancialRecord f where f.user.id =:userId GROUP BY(category)
            """)
    List<CategoryProjection> getCategorySummary(Long userId);

    @Query("""
            SELECT YEAR(f.date) as year,
            MONTH(f.date) as month,
            SUM(f.amount) as total
            FROM FinancialRecord f WHERE f.user.id =:userId GROUP BY YEAR(f.date), MONTH(f.date) ORDER BY year,month
          """)List<MonthlyProjection> getMonthlySummary(Long userId);
}
