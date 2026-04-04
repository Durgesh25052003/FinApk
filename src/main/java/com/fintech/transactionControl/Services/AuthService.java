package com.fintech.transactionControl.Services;

import com.fintech.transactionControl.DTOs.LoginDTO;
import com.fintech.transactionControl.DTOs.UserRequestDTO;
import com.fintech.transactionControl.DTOs.UserResponseDTO;
import com.fintech.transactionControl.JWT.JWTUtil;
import com.fintech.transactionControl.Repo.RoleRepo;
import com.fintech.transactionControl.Repo.UserRepo;
import com.fintech.transactionControl.entities.Role;
import com.fintech.transactionControl.entities.User;
import io.micrometer.observation.annotation.ObservationKeyValue;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

@Service
public class AuthService {
    private UserRepo userRepo;
    private RoleRepo roleRepo;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;

    public AuthService(UserRepo userRepo ,RoleRepo roleRepo,PasswordEncoder passwordEncoder ,AuthenticationManager authenticationManager,JWTUtil jwtUtil){
        this.userRepo=userRepo;
        this.roleRepo=roleRepo;
        this.passwordEncoder=passwordEncoder;
        this.authenticationManager=authenticationManager;
        this.jwtUtil=jwtUtil;
    }

    public UserResponseDTO Register(UserRequestDTO data){
        Map<String ,Object>response = new HashMap<>();
        User user = maptoEntity(data);
        UserResponseDTO userResponseDTO=maptoDTO(userRepo.save(user));
        return userResponseDTO;
    }

    //Login
    public ResponseEntity<Map<String,String>> Login(LoginDTO loginDTO){
        Map<String , String > message= new HashMap<>();
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDTO.getEmail(),
                        loginDTO.getPassword()
                )
        );
        User user =userRepo.findByEmail(loginDTO.getEmail()).orElseThrow(()-> new UsernameNotFoundException("User not Found.."));

        String token=jwtUtil.createToken(user.getEmail(),user.getRole());
        message.put("token",token);

        return new ResponseEntity<>(message, HttpStatus.OK);

    }


    public User maptoEntity(UserRequestDTO userRequestDTO){
        User user = new User();
        String rawPassword=userRequestDTO.getPassword();
        String hashedPassword= passwordEncoder.encode(rawPassword);
        Role role = roleRepo.findByName(userRequestDTO.getRole()).orElseThrow(()-> new RuntimeException("Role Not Found"));

        user.setEmail(userRequestDTO.getEmail());
        user.setName(userRequestDTO.getUsername());
        user.setPassword(hashedPassword);
        user.setRole(role);
        return user;
    }

    public UserResponseDTO maptoDTO(User userEntity){
        String role= userEntity.getRole();
        UserResponseDTO userResponseDTO=new UserResponseDTO();
        userResponseDTO.setUsername(userEntity.getName());
        userResponseDTO.setEmail(userEntity.getEmail());
        userResponseDTO.setRole(role);
        userResponseDTO.setActive(userEntity.getActive());
        return userResponseDTO;
    }
}
