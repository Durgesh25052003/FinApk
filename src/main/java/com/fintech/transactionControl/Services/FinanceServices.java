package com.fintech.transactionControl.Services;

import com.fintech.transactionControl.DTOs.FinancialRecordDTO;
import com.fintech.transactionControl.Exception.ForbiddenAccess;
import com.fintech.transactionControl.Exception.RecordNotFound;
import com.fintech.transactionControl.Repo.FinRecRepo;
import com.fintech.transactionControl.Repo.UserRepo;
import com.fintech.transactionControl.entities.FinancialRecord;
import com.fintech.transactionControl.entities.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class FinanceServices {

    private final FinRecRepo finRecRepo;
    private final UserRepo userRepo;
    public FinanceServices(FinRecRepo finRecRepo , UserRepo userRepo){
         this.finRecRepo=finRecRepo;
         this.userRepo=userRepo;
    }
    //Get All Financial Records
    public List<FinancialRecordDTO> getAllRecords(){
        List<FinancialRecordDTO> allRecords= finRecRepo.findAll().stream().map(this :: toDTO).toList();
        return allRecords;
    }
    public FinancialRecordDTO getById(Long id, Long userId) {

        FinancialRecord record = finRecRepo.findById(id)
                .orElseThrow(() -> new RecordNotFound("Record not Found"));

        // ownership check
        if (!record.getUser().getId().equals(userId)) {
            throw new ForbiddenAccess("You are not Allowed to Access this..");
        }

        return this.toDTO(record);
    }
    //Create Financial Record
    public ResponseEntity<String> createRecord(FinancialRecordDTO financialRecordDTO ,Long userId){
        User user = userRepo.findById(userId).orElseThrow(()-> new UsernameNotFoundException("User Not Found..."));
        FinancialRecord rec= FinanceServices.toEntity(financialRecordDTO, user);
        finRecRepo.save(rec);
        return  new ResponseEntity<>("Record Created Successfully" , HttpStatus.CREATED);
    }
  //Update Financial Record
    public ResponseEntity<Map<Object , String>>updateRecord(Long id , FinancialRecordDTO financialRecordDTO , Long userId){
        FinancialRecord rec= finRecRepo.findById(id).orElseThrow(()-> new RuntimeException());
        Map<Object , String> message =new HashMap<>();
        // Ownership Check
        if(!rec.getUser().getId().equals(userId)){
            message.put(new Object(){},"Invalid User");
            return new ResponseEntity<>(message ,HttpStatus.FORBIDDEN);
        }
        rec.setAmount(financialRecordDTO.getAmount());
        rec.setCategory(financialRecordDTO.getCategory());
        rec.setType(financialRecordDTO.getType());
        rec.setCategory(financialRecordDTO.getCategory());
        rec.setAmount(financialRecordDTO.getAmount());
        rec.setNotes(financialRecordDTO.getNotes());
        message.put(toDTO(rec),"Record Updated SuccessFully");
        return new ResponseEntity<>(message,HttpStatus.OK);
    }


    public void delete(Long id, Long userId) {

        FinancialRecord record = finRecRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Record not found"));

        // ownership check
        if (!record.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized to delete this record");
        }

        finRecRepo.delete(record);
    }
    public static FinancialRecord toEntity(FinancialRecordDTO dto, User user) {
        FinancialRecord record = new FinancialRecord();

        record.setAmount(dto.getAmount());
        record.setNotes(dto.getNotes());
        record.setType(dto.getType());
        record.setCategory(dto.getCategory());
        record.setDate(dto.getDate());
        record.setUser(user);
        return record;
    }

    public FinancialRecordDTO toDTO(FinancialRecord entity) {
        FinancialRecordDTO dto = new FinancialRecordDTO();
        dto.setAmount(entity.getAmount());
        dto.setType(entity.getType());
        dto.setNotes(entity.getNotes());
        dto.setCategory(entity.getCategory());
        dto.setDate(entity.getDate());
        return dto;
    }
}
