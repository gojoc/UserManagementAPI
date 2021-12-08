package ro.deloittedigital.samekh.usermanagement.controller;

import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ro.deloittedigital.samekh.usermanagement.exception.FieldAlreadyUsedException;
import ro.deloittedigital.samekh.usermanagement.exception.IncorrectCredentialsException;
import ro.deloittedigital.samekh.usermanagement.exception.UserNotFoundException;
import ro.deloittedigital.samekh.usermanagement.model.request.LoginRequest;
import ro.deloittedigital.samekh.usermanagement.model.request.RegisterOrUpdateRequest;
import ro.deloittedigital.samekh.usermanagement.model.response.GetCurrentUserResponse;
import ro.deloittedigital.samekh.usermanagement.model.response.GetLongProfileResponse;
import ro.deloittedigital.samekh.usermanagement.model.response.GetShortProfileResponse;
import ro.deloittedigital.samekh.usermanagement.model.response.LoginResponse;
import ro.deloittedigital.samekh.usermanagement.model.response.RegisterOrUpdateResponse;
import ro.deloittedigital.samekh.usermanagement.service.UserService;
import ro.deloittedigital.samekh.usermanagement.utility.JWTUtility;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.net.HttpURLConnection;
import java.net.URI;

@RestController
@Validated
@AllArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;
    private final UserDetailsService userDetailsService;
    private final JWTUtility jwtUtility;

    @Operation(summary = "Register user")
    @ApiResponses(value = {
            @ApiResponse(code = HttpURLConnection.HTTP_CREATED, message = "The user was registered successfully.",
                    response = RegisterOrUpdateResponse.class),
            @ApiResponse(code = HttpURLConnection.HTTP_BAD_REQUEST, message = "The request body is not valid."),
            @ApiResponse(code = HttpURLConnection.HTTP_CONFLICT, message = "The email or username was already used.")
    })
    @ResponseStatus(value = HttpStatus.CREATED)
    @PostMapping(path = "register", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RegisterOrUpdateResponse> register(@Valid @RequestBody RegisterOrUpdateRequest request)
            throws FieldAlreadyUsedException {
        log.info("[UserController] register user: {}", request);
        RegisterOrUpdateResponse response = userService.register(request);
        log.info("[UserController] registered user: {}", response);
        return ResponseEntity.created(URI.create(String.format("users/%s", response.getUsername()))).body(response);
    }

    @Operation(summary = "Login user")
    @ApiResponses(value = {
            @ApiResponse(code = HttpURLConnection.HTTP_OK, message = "The user was logged in successfully.",
                    response = LoginResponse.class),
            @ApiResponse(code = HttpURLConnection.HTTP_BAD_REQUEST, message = "The email is not valid."),
            @ApiResponse(code = HttpURLConnection.HTTP_UNAUTHORIZED, message = "The email or password is incorrect.")
    })
    @ResponseStatus(value = HttpStatus.OK)
    @PostMapping(path = "login", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) throws IncorrectCredentialsException {
        log.info("[UserController] login user: {}", request);
        userService.login(request);
        LoginResponse response = new LoginResponse(jwtUtility.generateJWT(userDetailsService
                .loadUserByUsername(request.getEmail())));
        log.info("[UserController] logged in user: {}", response);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get current user")
    @ApiResponses(value = {
            @ApiResponse(code = HttpURLConnection.HTTP_OK, message = "The current user was retrieved successfully.",
                    response = GetCurrentUserResponse.class),
            @ApiResponse(code = HttpURLConnection.HTTP_FORBIDDEN,
                    message = "You do not have permission to access this resource."),
            @ApiResponse(code = HttpURLConnection.HTTP_NOT_FOUND, message = "The user was not found.")
    })
    @ResponseStatus(value = HttpStatus.OK)
    @GetMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GetCurrentUserResponse> getCurrentUser() throws UserNotFoundException {
        log.info("[UserController] get current user: {}", userService.getCurrentEmail());
        GetCurrentUserResponse response = userService.getCurrentUser();
        log.info("[UserController] got current user: {}", response);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get user profile")
    @ApiResponses(value = {
            @ApiResponse(code = HttpURLConnection.HTTP_OK, message = "The user profile was retrieved successfully.",
                    response = GetLongProfileResponse.class),
            @ApiResponse(code = HttpURLConnection.HTTP_BAD_REQUEST, message = "The path variables are not valid."),
            @ApiResponse(code = HttpURLConnection.HTTP_FORBIDDEN,
                    message = "You do not have permission to access this resource."),
            @ApiResponse(code = HttpURLConnection.HTTP_NOT_FOUND, message = "The user was not found.")
    })
    @ResponseStatus(value = HttpStatus.OK)
    @GetMapping(path = "users/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GetShortProfileResponse> getProfile(@NotBlank @PathVariable String username)
            throws UserNotFoundException {
        log.info("[UserController] get user profile: {}", username);
        GetShortProfileResponse response = userService.getProfile(username);
        log.info("[UserController] got user profile: {}", response);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Update user information")
    @ApiResponses(value = {
            @ApiResponse(code = HttpURLConnection.HTTP_OK, message = "The user was updated successfully.",
                    response = RegisterOrUpdateResponse.class),
            @ApiResponse(code = HttpURLConnection.HTTP_BAD_REQUEST, message = "The request body is not valid."),
            @ApiResponse(code = HttpURLConnection.HTTP_FORBIDDEN,
                    message = "You do not have permission to access this resource."),
            @ApiResponse(code = HttpURLConnection.HTTP_NOT_FOUND, message = "The user was not found."),
            @ApiResponse(code = HttpURLConnection.HTTP_CONFLICT, message = "The email or username was already used.")
    })
    @ResponseStatus(value = HttpStatus.OK)
    @PutMapping(path = "users/{username}", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RegisterOrUpdateResponse> update(@NotBlank @PathVariable String username,
                                                           @Valid @RequestBody RegisterOrUpdateRequest request)
            throws UserNotFoundException, FieldAlreadyUsedException {
        log.info("[UserController] update user information: {} - {}", username, request);
        RegisterOrUpdateResponse response = userService.update(username, request);
        log.info("[UserService] updated user information: {} - {}", username, response);
        return ResponseEntity.ok(response);
    }
}
