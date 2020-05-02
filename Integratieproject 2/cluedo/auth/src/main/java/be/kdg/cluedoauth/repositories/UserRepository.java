package be.kdg.cluedoauth.repositories;

import be.kdg.cluedoauth.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<AppUser, UUID> {
    Optional<AppUser> findFirstByUsername(String username);
    boolean existsAppUserByUsernameOrEmail(String username, String email);
    boolean existsAppUserByUsername(String username);
    boolean existsAppUserByEmail(String email);
}
