package com.example.userverwaltung2.presentation.api;

import com.example.userverwaltung2.persistance.ClientRepository;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/api")
@Controller
public record UserAPI(ClientRepository userRepository, PasswordEncoder passwordEncoder,
                      UserDetailsService userDetailsService) {



}
