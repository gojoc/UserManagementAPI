package ro.deloittedigital.samekh.usermanagement.service.implementation;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ro.deloittedigital.samekh.usermanagement.exception.FieldAlreadyUsedException;
import ro.deloittedigital.samekh.usermanagement.exception.IncorrectCredentialsException;
import ro.deloittedigital.samekh.usermanagement.exception.UserNotFoundException;
import ro.deloittedigital.samekh.usermanagement.model.domain.User;
import ro.deloittedigital.samekh.usermanagement.model.domain.UserDetailsImplementation;
import ro.deloittedigital.samekh.usermanagement.model.request.LoginRequest;
import ro.deloittedigital.samekh.usermanagement.model.request.RegisterOrUpdateRequest;
import ro.deloittedigital.samekh.usermanagement.model.response.GetCurrentUserResponse;
import ro.deloittedigital.samekh.usermanagement.model.response.GetLongProfileResponse;
import ro.deloittedigital.samekh.usermanagement.model.response.GetShortProfileResponse;
import ro.deloittedigital.samekh.usermanagement.model.response.RegisterOrUpdateResponse;
import ro.deloittedigital.samekh.usermanagement.repository.UserRepository;
import ro.deloittedigital.samekh.usermanagement.service.UserService;

@Service
@AllArgsConstructor
@Slf4j
public class UserServiceImplementation implements UserService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    private void checkEmail(String email) throws FieldAlreadyUsedException {
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new FieldAlreadyUsedException(String.format("The email: '%s' was already used.", email));
        }
    }

    private void checkUsername(String username) throws FieldAlreadyUsedException {
        if (userRepository.existsByUsernameIgnoreCase(username)) {
            throw new FieldAlreadyUsedException(String.format("The username: '%s' was already used.", username));
        }
    }

    private User getUserByEmail(String email) throws UserNotFoundException {
        return userRepository.getByEmailIgnoreCase(email)
                .orElseThrow(() -> new UserNotFoundException(String.format("The user: '%s' was not found.", email)));
    }

    private User getUserByUsername(String username) throws UserNotFoundException {
        return userRepository.getByUsernameIgnoreCase(username)
                .orElseThrow(() -> new UserNotFoundException(String.format("The user: '%s' was not found.", username)));
    }

    @Override
    public String getCurrentEmail() {
        return ((UserDetailsImplementation) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal()).getUsername();
    }

    @Override
    public RegisterOrUpdateResponse register(RegisterOrUpdateRequest request) throws FieldAlreadyUsedException {
        log.info("[UserService] register user: {}", request);
        checkEmail(request.getEmail());
        checkUsername(request.getUsername());
        User user = modelMapper.map(request, User.class);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user = userRepository.save(user);
        RegisterOrUpdateResponse response = modelMapper.map(user, RegisterOrUpdateResponse.class);
        log.info("[UserService] registered user: {}", response);
        return response;
    }

    @Override
    public void login(LoginRequest request) throws IncorrectCredentialsException {
        log.info("[UserService] login user: {}", request);
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(),
                    request.getPassword()));
        } catch (BadCredentialsException exception) {
            throw new IncorrectCredentialsException();
        }
        log.info("[UserService] logged in user: {}", request);
    }

    @Override
    public GetCurrentUserResponse getCurrentUser() throws UserNotFoundException {
        final String email = getCurrentEmail();
        log.info("[UserService] get current user: {}", email);
        User user = getUserByEmail(email);
        GetCurrentUserResponse response = modelMapper.map(user, GetCurrentUserResponse.class);
        log.info("[UserService] got current user: {}", response);
        return response;
    }

    @Override
    public GetShortProfileResponse getProfile(String username) throws UserNotFoundException {
        log.info("[UserService] get user profile: {}", username);
        User user = getUserByUsername(username);
        final String email = getCurrentEmail();
        if (email.equalsIgnoreCase(user.getEmail())) {
            GetLongProfileResponse response = modelMapper.map(user, GetLongProfileResponse.class);
            log.info("[UserService] got user profile: {}", response);
            return response;
        } else {
            GetShortProfileResponse response = modelMapper.map(user, GetShortProfileResponse.class);
            log.info("[UserService] got user profile: {}", response);
            return response;
        }
    }

    @Override
    public RegisterOrUpdateResponse update(String username, RegisterOrUpdateRequest request)
            throws UserNotFoundException, FieldAlreadyUsedException {
        log.info("[UserService] update user information: {} - {}", username, request);
        User user = getUserByUsername(username);
        final String newEmail = request.getEmail();
        if (!newEmail.equals(user.getEmail())) {
            checkEmail(newEmail);
        }
        final String newUsername = request.getUsername();
        if (!newUsername.equals(user.getUsername())) {
            checkUsername(newUsername);
        }
        BeanUtils.copyProperties(request, user);
        user = userRepository.save(user);
        RegisterOrUpdateResponse response = modelMapper.map(user, RegisterOrUpdateResponse.class);
        log.info("[UserService] updated user information: {} - {}", username, response);
        return response;
    }
}
