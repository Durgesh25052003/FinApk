package com.fintech.transactionControl.Controller;

import com.fintech.transactionControl.DTOs.RoleUpdateDTO;
import com.fintech.transactionControl.DTOs.UserResponseDTO;
import com.fintech.transactionControl.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/finApk/user")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/getAllUsers")
    @PreAuthorize("hasAnyRole('ADMIN','ANALYST')")
    public List<UserResponseDTO> getAllUsers(){
        return userService.getAllUsers();
    }

    @PutMapping("/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> updateUserRole(@PathVariable Long id , @RequestBody RoleUpdateDTO roleUpdateDTO){
        return userService.updateUserRole(id ,roleUpdateDTO.getRole());
    }
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> toggleStatus(@PathVariable Long id) {
        userService.toggleActiveStatus(id);
        return ResponseEntity.ok("Status updated");
    }
    //Soft Deleting..
    @PutMapping("/{id}/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable Long id){
        userService.deleteUser(id);
;    }
}
