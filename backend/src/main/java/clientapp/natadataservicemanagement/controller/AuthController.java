package clientapp.natadataservicemanagement.controller;


import clientapp.natadataservicemanagement.dto.AuthResponseDto;
import clientapp.natadataservicemanagement.dto.LoginRequest;
import clientapp.natadataservicemanagement.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(
                authService.login(request.getUsername(), request.getPassword())
        );
    }
}
