//package com.example.violations.system.config;
//
//import com.example.violations.system.entity.Role;
//import com.example.violations.system.entity.User;
//import com.example.violations.system.repository.UserRepository;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//
//@Configuration
//public class Config {
//
//    private final BCryptPasswordEncoder passwordEncoder;
//
//    public Config(BCryptPasswordEncoder passwordEncoder) {
//        this.passwordEncoder = passwordEncoder;
//    }
//
//    @Bean
//    CommandLineRunner commandLineRunner(UserRepository userRepository) {
//        return args -> {
//            if (userRepository.findByEmail("a442020380@gmail.com").isEmpty()) {
//                User admin = new User();
//                admin.setFullName("Abdulaziz aljuhani");
//                admin.setEmail("a442020380@gmail.com");
//                admin.setPassword(passwordEncoder.encode("Azoz2002"));
//                admin.setRole(Role.ADMIN);
//
//                userRepository.save(admin); // Save without explicitly setting `id`
//            }
//        };
//    }
//
//}
package com.example.violations.system.config;

import com.example.violations.system.entity.Role;
import com.example.violations.system.entity.User;
import com.example.violations.system.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


@Configuration
public class Config {

    private final BCryptPasswordEncoder passwordEncoder;

    public Config(BCryptPasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    CommandLineRunner commandLineRunner(UserRepository userRepository) {
        return args -> {
            if (userRepository.findByEmail("a442020380@gmail.com").isEmpty()) {
                User admin = new User();
                admin.setFullName("Abdulaziz Aljuhani");
                admin.setEmail("a442020380@gmail.com");
                admin.setPassword(passwordEncoder.encode("Azoz2002"));
                admin.setRole(Role.ADMIN); // Ensure role is ADMIN

                userRepository.save(admin);
            }
        };
    }
}


