import clientapp.natadataservicemanagement.NataDataServiceManagementApplication;
import clientapp.natadataservicemanagement.controller.AuthController;
import clientapp.natadataservicemanagement.dto.AuthResponseDto;
import clientapp.natadataservicemanagement.security.JwtAuthenticationFilter;
import clientapp.natadataservicemanagement.security.JwtService;
import clientapp.natadataservicemanagement.security.SecurityConfig;
import clientapp.natadataservicemanagement.service.AuthService;
import clientapp.natadataservicemanagement.service.VaultService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@TestPropertySource(properties = {"SECURITY_USER_PASSWORD=admin123"})
@TestPropertySource(properties = {"SECURITY_USER_NAME=admin"})
@TestPropertySource(properties = {"VAULT_SECRET_TOKEN=dummy-token"})
@TestPropertySource(properties = {"TELEGRAM_BOT_TOKEN=dummy-token"})
@TestPropertySource(properties = {"TELEGRAM_WEBHOOK_SECRET=dummy-token"})
@TestPropertySource(properties = {"JWT_SECRET_KEY=dummy-token"})
@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration(classes = NataDataServiceManagementApplication.class)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;





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
                .thenThrow(new RuntimeException("Invalid username or password"));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "username": "admin",
                          "password": "wrong"
                        }
                        """))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.detail").value("Unpredictable Error , ask administrator for help"));
    }
}
