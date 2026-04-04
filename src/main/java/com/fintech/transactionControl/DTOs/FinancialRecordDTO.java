package com.fintech.transactionControl.DTOs;

import com.fintech.transactionControl.entities.RecordType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class FinancialRecordDTO {
    @NotNull
    @DecimalMin("0.01")
    private BigDecimal amount;

    @NotNull
    private RecordType type;

    private String category;

    @NotNull
    private LocalDate date;

    private String notes;

    public RecordType getType() {
        return type;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDate getDate() {
        return date;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setType(RecordType type) {
        this.type = type;
    }
}
