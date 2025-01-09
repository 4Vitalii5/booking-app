package org.cyberrealm.tech.security;

import lombok.RequiredArgsConstructor;
import org.cyberrealm.tech.exception.EntityNotFoundException;
import org.cyberrealm.tech.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) {
        return userRepository.findByEmail(username).orElseThrow(() ->
                new EntityNotFoundException("Can't find user by email:" + username)
        );
    }
}
