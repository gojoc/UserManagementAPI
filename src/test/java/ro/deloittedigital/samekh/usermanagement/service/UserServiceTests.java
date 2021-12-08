package ro.deloittedigital.samekh.usermanagement.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import ro.deloittedigital.samekh.usermanagement.exception.FieldAlreadyUsedException;
import ro.deloittedigital.samekh.usermanagement.model.domain.User;
import ro.deloittedigital.samekh.usermanagement.model.request.RegisterOrUpdateRequest;
import ro.deloittedigital.samekh.usermanagement.model.response.RegisterOrUpdateResponse;
import ro.deloittedigital.samekh.usermanagement.repository.UserRepository;
import ro.deloittedigital.samekh.usermanagement.service.implementation.UserServiceImplementation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static ro.deloittedigital.samekh.usermanagement.utility.UserConstants.EMAIL;
import static ro.deloittedigital.samekh.usermanagement.utility.UserConstants.FIRST_NAME;
import static ro.deloittedigital.samekh.usermanagement.utility.UserConstants.LAST_NAME;
import static ro.deloittedigital.samekh.usermanagement.utility.UserConstants.PASSWORD;
import static ro.deloittedigital.samekh.usermanagement.utility.UserConstants.USERNAME;

@ExtendWith(value = MockitoExtension.class)
class UserServiceTests {
    @Mock
    private UserRepository mockUserRepository;

    @Mock
    private ModelMapper mockModelMapper;

    @Mock
    private BCryptPasswordEncoder mockPasswordEncoder;

    @InjectMocks
    private UserServiceImplementation userService;

    @Test
    void givenUser_whenValid_thenRegisterShouldReturnRegisterOrUpdateResponse() throws FieldAlreadyUsedException {
        // Arrange
        User user = User.builder()
                .email(EMAIL)
                .password(PASSWORD)
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .username(USERNAME)
                .build();
        RegisterOrUpdateRequest request = RegisterOrUpdateRequest.builder()
                .email(EMAIL)
                .password(PASSWORD)
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .username(USERNAME)
                .build();
        RegisterOrUpdateResponse response = RegisterOrUpdateResponse.builder()
                .email(EMAIL)
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .username(USERNAME)
                .build();
        ;

        when(mockUserRepository.existsByEmailIgnoreCase(EMAIL))
                .thenReturn(false);
        when(mockUserRepository.existsByUsernameIgnoreCase(USERNAME))
                .thenReturn(false);
        when(mockUserRepository.save(user))
                .thenReturn(user);
        when(mockModelMapper.map(request, User.class))
                .thenReturn(user);
        when(mockModelMapper.map(user, RegisterOrUpdateResponse.class))
                .thenReturn(response);
        when(mockPasswordEncoder.encode(PASSWORD))
                .thenReturn(PASSWORD);

        // Act and Assert
        assertEquals(response, userService.register(request));
    }

    @Test
    void givenUser_whenEmailAlreadyUsed_thenRegisterShouldThrowFieldAlreadyUsedException() {
        // Arrange
        RegisterOrUpdateRequest request = RegisterOrUpdateRequest.builder()
                .email(EMAIL)
                .build();
        final String error = String.format("The email: '%s' was already used.", EMAIL);

        when(mockUserRepository.existsByEmailIgnoreCase(EMAIL))
                .thenReturn(true);

        // Act and Assert
        FieldAlreadyUsedException exception = assertThrows(FieldAlreadyUsedException.class, () ->
                userService.register(request));
        assertEquals(error, exception.getMessage());
    }
}
