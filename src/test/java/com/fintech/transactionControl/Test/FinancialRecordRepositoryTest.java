package com.fintech.transactionControl.Test;

import com.fintech.transactionControl.Projections.CategoryProjection;
import com.fintech.transactionControl.Repo.FinRecRepo;
import com.fintech.transactionControl.Repo.RoleRepo;
import com.fintech.transactionControl.Repo.UserRepo;
import com.fintech.transactionControl.entities.FinancialRecord;
import com.fintech.transactionControl.entities.RecordType;
import com.fintech.transactionControl.entities.Role;
import com.fintech.transactionControl.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class FinancialRecordRepositoryTest {

    @Autowired
    private FinRecRepo finRecRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private RoleRepo roleRepo;

    private User testUser;

    @BeforeEach
    void setUp() {
        // Create a test role and user for seeding records.
        Role userRole = new Role();
        userRole.setName("VIEWER");
        roleRepo.save(userRole);

        testUser = new User();
        testUser.setName("TestUser");
        testUser.setEmail("testuser@fintech.com");
        testUser.setPassword("hashedpassword");
        testUser.setRole(userRole);
        testUser.setActive(true);
        userRepo.save(testUser);
    }

    @Test
    void testSaveAndCalculateIncomeSumByType() {
        // Test: Save records with different types and verify SUM of INCOME is correct.
        FinancialRecord income1 = new FinancialRecord();
        income1.setAmount(new BigDecimal("1000.00"));
        income1.setType(RecordType.INCOME);
        income1.setCategory("salary");
        income1.setDate(LocalDate.of(2026, 1, 15));
        income1.setUser(testUser);
        finRecRepo.save(income1);

        FinancialRecord income2 = new FinancialRecord();
        income2.setAmount(new BigDecimal("500.00"));
        income2.setType(RecordType.INCOME);
        income2.setCategory("bonus");
        income2.setDate(LocalDate.of(2026, 2, 10));
        income2.setUser(testUser);
        finRecRepo.save(income2);

        FinancialRecord expense = new FinancialRecord();
        expense.setAmount(new BigDecimal("200.00"));
        expense.setType(RecordType.EXPENSE);
        expense.setCategory("groceries");
        expense.setDate(LocalDate.of(2026, 1, 20));
        expense.setUser(testUser);
        finRecRepo.save(expense);

        // Verify total income is 1500.00 (1000 + 500).
        BigDecimal totalIncome = finRecRepo.getTotalIncome(testUser.getId());
        assertEquals(new BigDecimal("1500.00"), totalIncome, "Total income should be 1500.00");
    }

    @Test
    void testFilterRecordsByDateRange() {
        // Test: Filter records by date range and verify only matching records are
        // returned.
        FinancialRecord record1 = new FinancialRecord();
        record1.setAmount(new BigDecimal("500.00"));
        record1.setType(RecordType.INCOME);
        record1.setCategory("freelance");
        record1.setDate(LocalDate.of(2026, 1, 10));
        record1.setUser(testUser);
        finRecRepo.save(record1);

        FinancialRecord record2 = new FinancialRecord();
        record2.setAmount(new BigDecimal("300.00"));
        record2.setType(RecordType.EXPENSE);
        record2.setCategory("rent");
        record2.setDate(LocalDate.of(2026, 2, 15));
        record2.setUser(testUser);
        finRecRepo.save(record2);

        FinancialRecord record3 = new FinancialRecord();
        record3.setAmount(new BigDecimal("100.00"));
        record3.setType(RecordType.EXPENSE);
        record3.setCategory("utilities");
        record3.setDate(LocalDate.of(2026, 3, 20));
        record3.setUser(testUser);
        finRecRepo.save(record3);

        // Query for records in February 2026.
        LocalDate startDate = LocalDate.of(2026, 2, 1);
        LocalDate endDate = LocalDate.of(2026, 2, 28);

        Specification<FinancialRecord> dateSpec = (root, query, cb) -> cb.between(root.get("date"), startDate, endDate);

        List<FinancialRecord> filteredRecords = finRecRepo.findAll(dateSpec);

        // Should find exactly one record in February.
        assertEquals(1, filteredRecords.size(), "Should find 1 record in date range");
        assertEquals(new BigDecimal("300.00"), filteredRecords.get(0).getAmount());
    }

    @Test
    void testGroupByCategoryAndCountRecords() {
        // Test: Group records by category and verify the count is correct.
        FinancialRecord rec1 = new FinancialRecord();
        rec1.setAmount(new BigDecimal("200.00"));
        rec1.setType(RecordType.EXPENSE);
        rec1.setCategory("groceries");
        rec1.setDate(LocalDate.of(2026, 1, 5));
        rec1.setUser(testUser);
        finRecRepo.save(rec1);

        FinancialRecord rec2 = new FinancialRecord();
        rec2.setAmount(new BigDecimal("150.00"));
        rec2.setType(RecordType.EXPENSE);
        rec2.setCategory("groceries");
        rec2.setDate(LocalDate.of(2026, 1, 12));
        rec2.setUser(testUser);
        finRecRepo.save(rec2);

        FinancialRecord rec3 = new FinancialRecord();
        rec3.setAmount(new BigDecimal("50.00"));
        rec3.setType(RecordType.EXPENSE);
        rec3.setCategory("transport");
        rec3.setDate(LocalDate.of(2026, 1, 8));
        rec3.setUser(testUser);
        finRecRepo.save(rec3);

        // Get category summary to verify grouping and totals.
        List<CategoryProjection> categorySummary = finRecRepo.getCategorySummary(testUser.getId());

        // Should have 2 categories: groceries and transport.
        assertEquals(2, categorySummary.size(), "Should have 2 categories");

        // Verify groceries total is 350.00 (200 + 150).
        CategoryProjection groceryRecord = categorySummary.stream()
                .filter(c -> "groceries".equals(c.getCategory()))
                .findFirst()
                .orElse(null);
        assertTrue(groceryRecord != null, "Groceries category should exist");
        assertEquals(new BigDecimal("350.00"), groceryRecord.getTotal(), "Groceries total should be 350.00");
    }
}
