package com.fintech.transactionControl.Services;

import com.fintech.transactionControl.DTOs.UserResponseDTO;
import com.fintech.transactionControl.Repo.RoleRepo;
import com.fintech.transactionControl.Repo.UserRepo;
import com.fintech.transactionControl.entities.Role;
import com.fintech.transactionControl.entities.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepo userRepo;
    private final RoleRepo roleRepo;
    private final AuthService authService;

    public UserService(UserRepo userRepo ,AuthService authService ,RoleRepo roleRepo){
        this.userRepo=userRepo;
        this.authService=authService;
        this.roleRepo=roleRepo;
    }

    public List<UserResponseDTO> getAllUsers(){
        List<UserResponseDTO> users = userRepo.findAll().stream().map(user ->  authService.maptoDTO(user)).toList();
        return users;
    }

    public ResponseEntity<String> updateUserRole(Long userId, String roleName) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Role role = roleRepo.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        user.setRole(role);
        userRepo.save(user);
        return new ResponseEntity<>("Role Updated Successfully", HttpStatus.OK);
    }
   public void toggleActiveStatus(long UserId){
        User user = userRepo.findById(UserId).orElseThrow(() -> new UsernameNotFoundException(" User Not found"));
        user.setActive(!user.getActive());
        userRepo.save(user);
   }

}
