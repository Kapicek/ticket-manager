package cz.upce.ticketmanager.auth;

import cz.upce.ticketmanager.auth.dto.ChangePasswordRequest;
import cz.upce.ticketmanager.auth.dto.JwtResponse;
import cz.upce.ticketmanager.auth.dto.LoginRequest;
import cz.upce.ticketmanager.auth.dto.RegisterRequest;
import cz.upce.ticketmanager.user.User;
import cz.upce.ticketmanager.user.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final AuthenticationManager authManager;
    private final JwtService jwt;

    public AuthService(UserRepository users, PasswordEncoder encoder,
                       AuthenticationManager authManager, JwtService jwt) {
        this.users = users;
        this.encoder = encoder;
        this.authManager = authManager;
        this.jwt = jwt;
    }

    @Transactional
    public void register(RegisterRequest req){
        if (users.existsByUsername(req.username()))
            throw new IllegalArgumentException("Username already used");
        if (users.existsByEmail(req.email()))
            throw new IllegalArgumentException("Email already used");

        var u = User.builder()
                .username(req.username())
                .email(req.email())
                .password(encoder.encode(req.password()))
                .build();
        users.save(u);
    }

    public JwtResponse login(LoginRequest req){
        try {
            authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.login(), req.password())
            );
        } catch (AuthenticationException ex) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }
        String token = jwt.generateToken(req.login());
        return new JwtResponse(token, jwt.getExpiresInSeconds());
    }

    public void changePassword(User current, ChangePasswordRequest req) {
        if (!encoder.matches(req.oldPassword(), current.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Old password invalid");
        }
        current.setPassword(encoder.encode(req.newPassword()));
        users.save(current);
    }
}
