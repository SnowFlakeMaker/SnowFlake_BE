package sookmyung.noonsongmaker.Repository;

import org.springframework.stereotype.Repository;
import sookmyung.noonsongmaker.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);
}
