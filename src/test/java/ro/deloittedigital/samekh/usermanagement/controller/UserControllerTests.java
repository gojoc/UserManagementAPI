package ro.deloittedigital.samekh.usermanagement.controller;


import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import ro.deloittedigital.samekh.usermanagement.model.request.LoginRequest;
import ro.deloittedigital.samekh.usermanagement.model.request.RegisterOrUpdateRequest;
import ro.deloittedigital.samekh.usermanagement.model.response.GetLongProfileResponse;
import ro.deloittedigital.samekh.usermanagement.model.response.LoginResponse;
import ro.deloittedigital.samekh.usermanagement.model.response.RegisterOrUpdateResponse;
import ro.deloittedigital.samekh.usermanagement.utility.UserConstants;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
class UserControllerTests {
    private static final String REGISTER_PATH = "register";
    private static final String LOGIN_PATH = "login";
    private final WebTestClient webTestClient;

    @Test
    void givenUser_whenValid_thenRegisterShouldReturnCreatedStatus() {
        // Arrange
        RegisterOrUpdateRequest request = RegisterOrUpdateRequest.builder()
                .email(UserConstants.EMAIL)
                .password(UserConstants.PASSWORD)
                .firstName(UserConstants.FIRST_NAME)
                .lastName(UserConstants.LAST_NAME)
                .username(UserConstants.USERNAME)
                .build();
        RegisterOrUpdateResponse response = RegisterOrUpdateResponse.builder()
                .email(UserConstants.EMAIL)
                .firstName(UserConstants.FIRST_NAME)
                .lastName(UserConstants.LAST_NAME)
                .username(UserConstants.USERNAME)
                .build();

        // Act and Assert
        webTestClient.post()
                .uri(REGISTER_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), RegisterOrUpdateRequest.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(RegisterOrUpdateResponse.class)
                .isEqualTo(response);
    }

    @Test
    void givenUser_whenEmailAlreadyUsed_thenRegisterShouldReturnConflictStatus() {
        // Arrange
        final String email = "john.doe@mail.com";
        final String username = "john.doe";
        final String error = String.format("[\"The email: '%s' was already used.\"]", email);
        RegisterOrUpdateRequest request = RegisterOrUpdateRequest.builder()
                .email(email)
                .password(UserConstants.PASSWORD)
                .firstName(UserConstants.FIRST_NAME)
                .lastName(UserConstants.LAST_NAME)
                .username(username)
                .build();

        webTestClient.post()
                .uri(REGISTER_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), RegisterOrUpdateRequest.class)
                .exchange();

        // Act and Assert
        webTestClient.post()
                .uri(REGISTER_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), RegisterOrUpdateRequest.class)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CONFLICT)
                .expectBody(String.class)
                .isEqualTo(error);
    }

    @Test
    void givenUser_whenIsSameUser_thenGetProfileShouldReturnOkStatus() {
        // Arrange
        final String email = "judy.doe@mail.com";
        final String username = "judy.doe";
        RegisterOrUpdateRequest registerRequest = RegisterOrUpdateRequest.builder()
                .email(email)
                .password(UserConstants.PASSWORD)
                .firstName(UserConstants.FIRST_NAME)
                .lastName(UserConstants.LAST_NAME)
                .username(username)
                .build();
        LoginRequest loginRequest = new LoginRequest(email, UserConstants.PASSWORD);
        GetLongProfileResponse getLongProfileResponse = GetLongProfileResponse.builder()
                .email(email)
                .firstName(UserConstants.FIRST_NAME)
                .lastName(UserConstants.LAST_NAME)
                .build();

        webTestClient.post()
                .uri(REGISTER_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(registerRequest), RegisterOrUpdateRequest.class)
                .exchange();

        LoginResponse loginResponse = webTestClient.post()
                .uri(LOGIN_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(loginRequest), LoginRequest.class)
                .exchange()
                .expectBody(LoginResponse.class)
                .returnResult()
                .getResponseBody();
        assertNotNull(loginResponse);

        final String getProfilePath = String.format("users/%s", username);
        final String bearer = String.format("Bearer %s", loginResponse.getJwt());

        // Act and Assert
        webTestClient.get()
                .uri(getProfilePath)
                .header(HttpHeaders.AUTHORIZATION, bearer)
                .exchange()
                .expectStatus().isOk()
                .expectBody(GetLongProfileResponse.class)
                .isEqualTo(getLongProfileResponse);
    }

    @Test
    void givenUser_whenUserDoes_thenGetProfileShouldReturnOkStatus() {
        // Arrange
        final String getProfilePath = String.format("users/%s", UserConstants.USERNAME);
        final String error = "[\"You do not have permission to access this resource.\"]";

        // Act and Assert
        webTestClient.get()
                .uri(getProfilePath)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody(String.class)
                .isEqualTo(error);
    }
}
