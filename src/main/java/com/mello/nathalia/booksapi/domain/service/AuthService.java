package com.mello.nathalia.booksapi.domain.service;

import com.mello.nathalia.booksapi.api.request.LoginRequest;
import com.mello.nathalia.booksapi.api.request.RegisterRequest;
import com.mello.nathalia.booksapi.api.response.AuthResponse;
import com.mello.nathalia.booksapi.common.exception.EmailAlreadyExistsException;
import com.mello.nathalia.booksapi.domain.model.Role;
import com.mello.nathalia.booksapi.domain.model.User;
import com.mello.nathalia.booksapi.domain.repository.UserRepository;
import com.mello.nathalia.booksapi.infrastructure.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException(
                    "Já existe uma conta com o email: " + request.email()
            );
        }

        User user = new User();
        user.setName(request.name());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(Role.USER);

        userRepository.save(user);
        log.info("Novo usuário registrado: {}", request.email());

        String token = jwtService.generateToken(user);
        return new AuthResponse(token, user.getName(), user.getEmail(), user.getRole().name());
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

        log.info("Login realizado: {}", request.email());

        String token = jwtService.generateToken(user);
        return new AuthResponse(token, user.getName(), user.getEmail(), user.getRole().name());
    }
}
