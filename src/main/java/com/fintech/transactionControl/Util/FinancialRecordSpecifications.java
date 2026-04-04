package com.fintech.transactionControl.Util;

import com.fintech.transactionControl.entities.FinancialRecord;
import com.fintech.transactionControl.entities.RecordType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class FinancialRecordSpecifications {
    public static Specification<FinancialRecord>filter(LocalDate startDate, LocalDate endDate, RecordType type , String category){
        return (root,query,cb)->{
            List<Predicate> predicates=new ArrayList<>();
            if(startDate!=null){
                predicates.add(cb.greaterThanOrEqualTo(root.get("date"),startDate));
            }
            if(endDate!=null){
                predicates.add(cb.lessThanOrEqualTo(root.get("date"),endDate));
            }
            if(type!=null){
                predicates.add(cb.equal(root.get("type"),type));
            }
            if(category!=null){
                predicates.add(cb.equal(root.get("category"),category));
            }
            return cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };
    }
}
