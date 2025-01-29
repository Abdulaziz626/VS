package com.example.violations.system.util;

import com.example.violations.system.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import com.example.violations.system.repository.UserRepository;

@Component
@RequiredArgsConstructor
public class AuthUtil{

    private final UserRepository userRepository;

        public User getAuthenticatedUser() {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal instanceof UserDetails) {
                String username = ((UserDetails) principal).getUsername();
                return userRepository.findByEmail(username).orElseThrow(()->
                        new UsernameNotFoundException("The User not found"));
            }
            return null;
        }


}
