package clientapp.natadataservicemanagement.controller;
import clientapp.natadataservicemanagement.dto.AuthResponseDto;
import clientapp.natadataservicemanagement.security.JwtService;
import clientapp.natadataservicemanagement.service.AuthService;
import org.junit.jupiter.api.Test;

import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(properties = {
        "SECURITY_USER_NAME=admin",
        "SECURITY_USER_PASSWORD=admin123",
        "JWT_SECRET_KEY=dummy-token",
        "SPRING_CLOUD_VAULT_URI=http://localhost:8200",
        "VAULT_SECRET_TOKEN=dummy-token"
})
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtService jwtService;





    @Test
    void login_shouldReturnJwtToken_whenCredentialsAreValid() throws Exception {

        when(authService.login(anyString(), anyString()))
                .thenReturn(new AuthResponseDto("jwt-token-123"));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "username": "admin",
                          "password": "password"
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token-123"));
    }

    @Test
    void login_shouldReturn401_whenCredentialsAreInvalid() throws Exception {

        when(authService.login(anyString(), anyString()))
                .thenThrow(new BadCredentialsException("Invalid username or password"));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "username": "admin",
                          "password": "wrong"
                        }
                        """))
                .andExpect(status().isInternalServerError());

    }
}
