package com.fintech.transactionControl.Controller;

import com.fintech.transactionControl.Projections.CategoryProjection;
import com.fintech.transactionControl.Repo.FinRecRepo;
import com.fintech.transactionControl.Util.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/finApk/analytics")
public class DashboardAPIController {
    @Autowired
    private FinRecRepo finRecRepo;
    //Category
    @GetMapping("/category")
    @PreAuthorize("hasAnyRole('ADMIN' , 'ANALYST' , 'VIEWER')")
    public ResponseEntity<?> getCategorySummary(@AuthenticationPrincipal CustomUserDetails userDetails){
        return ResponseEntity.ok(finRecRepo.getCategorySummary(userDetails.getId()));
    }
    //Monthly
    @GetMapping("/monthly")
    public ResponseEntity<?> getMonthly(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return ResponseEntity.ok(
                finRecRepo.getMonthlySummary(userDetails.getId())
        );
    }
    //Summary
    @GetMapping("/summary")
    public ResponseEntity<?>getSummary( @AuthenticationPrincipal CustomUserDetails userDetails){
        BigDecimal Incometotal = finRecRepo.getTotalIncome(userDetails.getId());
        BigDecimal Expensetotal = finRecRepo.getTotalExpense(userDetails.getId());
        BigDecimal balance=Incometotal.subtract(Expensetotal);

        return ResponseEntity.ok(
                Map.of(
                        "totalIncome", Incometotal,
                        "totalExpense", Expensetotal,
                        "balance", balance
                )
        );
    }
    @GetMapping("/recent")
    @PreAuthorize("hasAnyRole('ADMIN' , 'ANALYST' , 'VIEWER')")
    public ResponseEntity<?>getRecent(@AuthenticationPrincipal CustomUserDetails userDetails , @PageableDefault(size=5,sort="date",direction= Sort.Direction.DESC) Pageable page){
        return ResponseEntity.ok(finRecRepo.findByUser_Id(userDetails.getId(),page));
    }
}
