package ro.deloittedigital.samekh.usermanagement.service.implementation;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ro.deloittedigital.samekh.usermanagement.model.domain.User;
import ro.deloittedigital.samekh.usermanagement.model.domain.UserDetailsImplementation;
import ro.deloittedigital.samekh.usermanagement.repository.UserRepository;

@Service
@AllArgsConstructor
@Slf4j
public class UserDetailsServiceImplementation implements UserDetailsService {
    private final UserRepository userRepository;

    private User getUserByEmail(String email) throws UsernameNotFoundException {
        return userRepository.getByEmailIgnoreCase(email)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("The user: '%s' was not found.", email)));
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.info("[UserDetailsService] get user: {}", email);
        User user = getUserByEmail(email);
        log.info("[UserDetailsService] got user: {}", email);
        return new UserDetailsImplementation(user);
    }
}
