package ro.deloittedigital.samekh.usermanagement.repository;

import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ro.deloittedigital.samekh.usermanagement.model.domain.User;
import ro.deloittedigital.samekh.usermanagement.utility.UserConstants;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AllArgsConstructor(onConstructor_ = {@Autowired})
class UserRepositoryTests {
    private final UserRepository userRepository;

    @Test
    void givenEmail_whenUserExists_thenExistsByEmailIgnoreCaseShouldReturnTrue() {
        // Arrange
        userRepository.save(User.builder()
                .email(UserConstants.EMAIL)
                .password(UserConstants.PASSWORD)
                .firstName(UserConstants.FIRST_NAME)
                .lastName(UserConstants.LAST_NAME)
                .username(UserConstants.USERNAME)
                .build());

        // Act and Assert
        assertTrue(userRepository.existsByEmailIgnoreCase(UserConstants.EMAIL));
    }

    @Test
    void givenEmail_whenUserNotExists_thenExistsByEmailIgnoreCaseShouldReturnFalse() {
        // Act and Assert
        assertFalse(userRepository.existsByEmailIgnoreCase(UserConstants.EMAIL));
    }
}
