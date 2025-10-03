package cz.upce.ticketmanager.common;

import cz.upce.ticketmanager.user.User;
import cz.upce.ticketmanager.user.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class CurrentUser {
    private final UserRepository users;
    public CurrentUser(UserRepository users) { this.users = users; }
    public User get(Authentication auth) {
        return users.findByUsername(auth.getName()).orElseThrow();
    }
}
