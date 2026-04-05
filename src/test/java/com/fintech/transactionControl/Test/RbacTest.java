package com.fintech.transactionControl.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fintech.transactionControl.Config.SecurityConfig;
import com.fintech.transactionControl.Controller.FinanceController;
import com.fintech.transactionControl.JWT.JWTFilter;
import com.fintech.transactionControl.Repo.FinRecRepo;
import com.fintech.transactionControl.Services.CustomUserDetailsService;
import com.fintech.transactionControl.Services.FinanceServices;
import com.fintech.transactionControl.Util.CustomUserDetails;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest(controllers = FinanceController.class)
@Import(SecurityConfig.class)
@org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
public class RbacTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private FinanceServices financeServices;

    @MockitoBean
    private FinRecRepo finRecRepo;

    @MockitoBean
    private JWTFilter jwtAuthFilter;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminCanPostRecord_shouldReturnCreated() throws Exception {
        // An ADMIN user is allowed to create a financial record.
        CustomUserDetails adminUser = new CustomUserDetails(
                1L,
                "admin",
                "password",
                AuthorityUtils.createAuthorityList("ROLE_ADMIN"),
                true);

        String requestBody = objectMapper.writeValueAsString(Map.of(
                "amount", 100.00,
                "type", "INCOME",
                "date", "2026-04-05",
                "category", "salary",
                "notes", "admin created record"));

        mockMvc.perform(post("/api/v1/finApk/fin/create")
                .with(user(adminUser))
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "VIEWER")
    void viewerCannotPostRecord_shouldReturnForbidden() throws Exception {
        // A VIEWER user should be rejected if the endpoint is restricted to ADMIN-only.
        CustomUserDetails viewerUser = new CustomUserDetails(
                2L,
                "viewer",
                "password",
                AuthorityUtils.createAuthorityList("ROLE_VIEWER"),
                true);

        String requestBody = objectMapper.writeValueAsString(Map.of(
                "amount", 50.00,
                "type", "EXPENSE",
                "date", "2026-04-05",
                "category", "travel",
                "notes", "viewer attempt"));

        mockMvc.perform(post("/api/v1/finApk/fin/create")
                .with(user(viewerUser))
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isForbidden());
    }

    @Test
    void unauthenticatedPostRecord_shouldReturnUnauthorized() throws Exception {
        // Requests without any authentication should be rejected with 401.
        String requestBody = objectMapper.writeValueAsString(Map.of(
                "amount", 32.50,
                "type", "EXPENSE",
                "date", "2026-04-05",
                "category", "office",
                "notes", "no auth"));

        mockMvc.perform(post("/api/v1/finApk/fin/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isUnauthorized());
    }
}
