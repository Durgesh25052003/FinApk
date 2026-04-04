package com.fintech.transactionControl.Controller;

import com.fintech.transactionControl.DTOs.FinancialRecordDTO;
import com.fintech.transactionControl.Repo.FinRecRepo;
import com.fintech.transactionControl.Services.CustomUserDetailsService;
import com.fintech.transactionControl.Services.FinanceServices;
import com.fintech.transactionControl.Util.CustomUserDetails;
import com.fintech.transactionControl.Util.FinancialRecordSpecifications;
import com.fintech.transactionControl.entities.FinancialRecord;
import com.fintech.transactionControl.entities.RecordType;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/finApk/fin")
public class FinanceController {

    private final FinRecRepo finRecRepo;
    private final FinanceServices financeServices;
    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    public FinanceController(FinRecRepo finRecRepo,FinanceServices financeServices){
        this.finRecRepo=finRecRepo;
        this.financeServices=financeServices;
    }

    @GetMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public Page<FinancialRecord> filter(@RequestParam(required = false) LocalDate startDate, @RequestParam(required = false)LocalDate endDate, @RequestParam(required = false) RecordType type, @RequestParam(required = false) String category , @PageableDefault(size = 10, sort = "date", direction = Sort.Direction.DESC) Pageable pageable){
        Specification<FinancialRecord> spec= FinancialRecordSpecifications.filter(startDate,endDate, type, category);

        return finRecRepo.findAll(spec,pageable);
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('ADMIN' , 'ANALYST' , 'VIEWER')")
    public List<FinancialRecordDTO> getAllUsers(){
        return financeServices.getAllRecords();
    }

    @GetMapping("/getRecordById/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ANALYST','VIEWER')")
    public ResponseEntity<FinancialRecordDTO> getById(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {

        FinancialRecordDTO dto = financeServices.getById(id, userDetails.getId());

        return ResponseEntity.ok(dto);
    }
    // ✅ CREATE
    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ADMIN' , 'VIEWER', 'ANALYST')")
    public ResponseEntity<String> create(
            @RequestBody @Valid FinancialRecordDTO dto,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        ResponseEntity<String> created = financeServices.createRecord(dto, userDetails.getId());
        return new ResponseEntity<>("Record Created Successfully",HttpStatus.CREATED);
    }

    // 🔄 UPDATE
    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseEntity<Map<Object, String>>> update(
            @PathVariable Long id,
            @RequestBody @Valid FinancialRecordDTO dto,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        ResponseEntity<Map<Object, String>> updated = financeServices.updateRecord(id, dto, userDetails.getId());
        return ResponseEntity.ok(updated);
    }

    // ❌ DELETE
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        financeServices.delete(id, userDetails.getId());
        return ResponseEntity.ok("Record deleted successfully");
    }
}
