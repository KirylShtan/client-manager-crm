package clientapp.natadataservicemanagement.controller;


import clientapp.natadataservicemanagement.dto.AuthResponseDto;
import clientapp.natadataservicemanagement.security.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager
            ,UserDetailsService userDetailsService, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtService = jwtService;
    }
    @Operation(summary = "Login operation")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = AuthResponseDto.class)
            )),
            @ApiResponse(responseCode = "401", description = "Unauthorized",content =@Content(
               mediaType = "application/json",
               schema = @Schema(implementation = ProblemDetail.class)
            )),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class)
            ))
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            logger.info("\"POST /auth/login - authentication attempt\"");
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.username, request.password)
            );
            logger.info("trying to load the service using username");
            UserDetails user = userDetailsService.loadUserByUsername(request.username);
            logger.info("Getting fresh token by using jwtService");
            String token = jwtService.generateToken(user);
            return ResponseEntity.ok(new AuthResponseDto(token));
        } catch (AuthenticationException e) {
            logger.warn("Authorization failed, try again");
            ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, "Invalid username or password");
                    pd.setTitle("Authentication failed");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(pd);
        }
    }

    public static class LoginRequest {
        public String username;
        public String password;
    }
}