package cz.upce.ticketmanager.auth;

import cz.upce.ticketmanager.auth.dto.ChangePasswordRequest;
import cz.upce.ticketmanager.auth.dto.JwtResponse;
import cz.upce.ticketmanager.auth.dto.LoginRequest;
import cz.upce.ticketmanager.auth.dto.RegisterRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService service;

    public AuthController(AuthService service) { this.service = service; }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid @RequestBody RegisterRequest req){
        service.register(req);
        return ResponseEntity.status(201).build();
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody LoginRequest req){
        return ResponseEntity.ok(service.login(req));
    }

}
