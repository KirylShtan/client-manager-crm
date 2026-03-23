import clientapp.natadataservicemanagement.NataDataServiceManagementApplication;
import clientapp.natadataservicemanagement.controller.AuthController;
import clientapp.natadataservicemanagement.security.JwtService;
import clientapp.natadataservicemanagement.security.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@TestPropertySource(properties = {"SECURITY_USER_PASSWORD=admin123"})
@TestPropertySource(properties = {"SECURITY_USER_NAME=admin"})
@WebMvcTest(controllers = AuthController.class)
@ContextConfiguration(classes = NataDataServiceManagementApplication.class)
@Import(SecurityConfig.class)
public class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private JwtService jwtService;


//    @Test
//    void login_shouldReturnJwtToken_whenCredentialsAreValid() throws Exception {
//        AuthController.LoginRequest request = new AuthController.LoginRequest();
//        request.username = "admin";
//        request.password = "password";
//
//        UserDetails userDetails = org.springframework.security.core.userdetails.User
//                .withUsername("admin")
//                .password("password")
//                .roles("ADMIN")
//                .build();
//
//        Authentication authentication = Mockito.mock(Authentication.class);
//
//        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
//                .thenReturn(authentication);
//
//        when(userDetailsService.loadUserByUsername("admin"))
//                .thenReturn(userDetails);
//
//        when(jwtService.generateToken(userDetails))
//                .thenReturn("jwt-token-123");
//
//        mockMvc.perform(post("/auth/login")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content("""
//                        {
//                          "username": "admin",
//                          "password": "password"
//                        }
//                        """))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.token").value("jwt-token-123"));
//    }

    @Test
    void login_shouldReturn401_whenCredentialsAreInvalid() throws Exception {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new org.springframework.security.authentication.BadCredentialsException("Bad credentials"));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "username": "admin",
                          "password": "wrong"
                        }
                        """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.detail").value("Invalid username or password"));
    }

}
