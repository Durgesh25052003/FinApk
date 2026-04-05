package com.fintech.transactionControl.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fintech.transactionControl.Controller.AuthController;
import com.fintech.transactionControl.DTOs.LoginDTO;
import com.fintech.transactionControl.DTOs.UserRequestDTO;
import com.fintech.transactionControl.DTOs.UserResponseDTO;
import com.fintech.transactionControl.Services.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import java.util.Map;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
public class AuthTesting {

  private MockMvc mockMvc;

  @Mock
  private AuthService authService;

  private ObjectMapper objectMapper = new ObjectMapper();

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.standaloneSetup(new AuthController(authService)).build();
  }

  // TEST 1 Successful Registration

  @Test
  void registerWithValidInput_shouldReturn200andUserResponse() throws Exception {
    // Arrange
    UserRequestDTO request = new UserRequestDTO();
    request.setUsername("random");
    request.setEmail("dns@fintech.com");
    request.setPassword("dns123");
    request.setRole("VIEWER");

    UserResponseDTO expectedResponse = new UserResponseDTO("random", "dns@fintech.com", "VIEWER", true);

    Mockito.when(authService.Register(Mockito.any(UserRequestDTO.class))).thenReturn(expectedResponse);

    // Act & Assert
    mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/finApk/auth/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.username").value("random"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("dns@fintech.com"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.role").value("VIEWER"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.active").value(true));
  }

  // TEST 2 Successful Login

  @Test
  void loginWithValidCredentials_shouldReturn200andToken() throws Exception {
    // Arrange
    LoginDTO loginRequest = new LoginDTO();
    loginRequest.setEmail("dns@fintech.com");
    loginRequest.setPassword("dns123");

    Map<String, String> expectedResponse = Map.of("token", "mocked-jwt-token");

    Mockito.when(authService.Login(Mockito.any(LoginDTO.class))).thenReturn(ResponseEntity.ok(expectedResponse));

    // Act & Assert
    mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/finApk/auth/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(loginRequest)))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.token").value("mocked-jwt-token"));
  }

  // TEST 3 Invalid Login

  @Test
  void loginWithInvalidCredentials_shouldReturn401andErrorMessage() throws Exception {
    // Arrange
    LoginDTO loginRequest = new LoginDTO();
    loginRequest.setEmail("wrong@fintech.com");
    loginRequest.setPassword("badpassword");

    Map<String, String> errorResponse = Map.of("error", "Invalid credentials");

    Mockito.when(authService.Login(Mockito.any(LoginDTO.class)))
        .thenReturn(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse));

    // Act & Assert
    mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/finApk/auth/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(loginRequest)))
        .andExpect(MockMvcResultMatchers.status().isUnauthorized())
        .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("Invalid credentials"));
  }
}
