package ro.deloittedigital.samekh.usermanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ro.deloittedigital.samekh.usermanagement.model.domain.User;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    boolean existsByEmailIgnoreCase(String email);

    boolean existsByUsernameIgnoreCase(String username);

    Optional<User> getByEmailIgnoreCase(String email);

    Optional<User> getByUsernameIgnoreCase(String username);
}
