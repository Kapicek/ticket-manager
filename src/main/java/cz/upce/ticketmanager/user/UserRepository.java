package cz.upce.ticketmanager.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    default Optional<User> findByUsernameOrEmail(String login){
        return findByUsername(login).or(() -> findByEmail(login));
    }
}
