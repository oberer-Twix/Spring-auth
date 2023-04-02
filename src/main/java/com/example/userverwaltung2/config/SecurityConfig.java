package com.example.userverwaltung2.config;

import com.example.userverwaltung2.domain.Rolle;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.security.SecureRandom;

@EnableWebSecurity
@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10, new SecureRandom());
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http)
            throws Exception {
        http
                .csrf()
                .and()
                // alle Routen nur für Authentifizierte
                .authorizeHttpRequests(auth ->
                                auth.requestMatchers("/", "/register", "/login", "/style.css").permitAll()
                                        .requestMatchers(HttpMethod.POST, "/register", "/login").permitAll()
                                        .requestMatchers("/admin/**").hasRole(String.valueOf(Rolle.ADMIN))
                                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .permitAll()
                        .defaultSuccessUrl("/overview", true)
                ).rememberMe();
        return http.build();
    }
}