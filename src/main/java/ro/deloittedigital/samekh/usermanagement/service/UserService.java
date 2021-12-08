package ro.deloittedigital.samekh.usermanagement.service;

import ro.deloittedigital.samekh.usermanagement.exception.FieldAlreadyUsedException;
import ro.deloittedigital.samekh.usermanagement.exception.IncorrectCredentialsException;
import ro.deloittedigital.samekh.usermanagement.exception.UserNotFoundException;
import ro.deloittedigital.samekh.usermanagement.model.request.LoginRequest;
import ro.deloittedigital.samekh.usermanagement.model.request.RegisterOrUpdateRequest;
import ro.deloittedigital.samekh.usermanagement.model.response.GetCurrentUserResponse;
import ro.deloittedigital.samekh.usermanagement.model.response.GetShortProfileResponse;
import ro.deloittedigital.samekh.usermanagement.model.response.RegisterOrUpdateResponse;

public interface UserService {
    String getCurrentEmail();

    RegisterOrUpdateResponse register(RegisterOrUpdateRequest request) throws FieldAlreadyUsedException;

    void login(LoginRequest request) throws IncorrectCredentialsException;

    GetCurrentUserResponse getCurrentUser() throws UserNotFoundException;

    GetShortProfileResponse getProfile(String username) throws UserNotFoundException;

    RegisterOrUpdateResponse update(String username, RegisterOrUpdateRequest request) throws UserNotFoundException,
            FieldAlreadyUsedException;
}
