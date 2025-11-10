package cz.upce.ticketmanager.auth;

import cz.upce.ticketmanager.auth.dto.ChangePasswordRequest;
import cz.upce.ticketmanager.auth.dto.JwtResponse;
import cz.upce.ticketmanager.auth.dto.LoginRequest;
import cz.upce.ticketmanager.auth.dto.RegisterRequest;
import cz.upce.ticketmanager.common.CurrentUser;
import cz.upce.ticketmanager.user.User;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService service;
    private final CurrentUser current;

    public AuthController(AuthService service, CurrentUser current) {
        this.service = service;
        this.current = current;
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid @RequestBody RegisterRequest req){
        service.register(req);
        return ResponseEntity.status(201).build();
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody LoginRequest req){
        return ResponseEntity.ok(service.login(req));
    }

    @PostMapping("/change-password")
    public ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordRequest req,
                                               Authentication auth) {
        var user = current.get(auth);
        service.changePassword(user, req);
        return ResponseEntity.noContent().build();
    }
}
