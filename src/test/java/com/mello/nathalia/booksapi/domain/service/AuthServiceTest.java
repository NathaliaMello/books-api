package com.mello.nathalia.booksapi.domain.service;

import com.mello.nathalia.booksapi.api.request.LoginRequest;
import com.mello.nathalia.booksapi.api.request.RegisterRequest;
import com.mello.nathalia.booksapi.api.response.AuthResponse;
import com.mello.nathalia.booksapi.common.exception.EmailAlreadyExistsException;
import com.mello.nathalia.booksapi.domain.model.Role;
import com.mello.nathalia.booksapi.domain.model.User;
import com.mello.nathalia.booksapi.domain.repository.UserRepository;
import com.mello.nathalia.booksapi.infrastructure.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    private User user;
    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setEmail("joao@email.com");
        user.setName("João");
        user.setPassword("senha_codificada");
        user.setRole(Role.USER);

        registerRequest = new RegisterRequest(
                "Maria", "maria@email.com", "123456"
        );

        loginRequest = new LoginRequest("joao@email.com", "123456");

    }

    @Test
    @DisplayName("deve lançar EmailAlreadyExistsException quando email já cadastrado")
    void register_deveLancarEmailAlreadyExistsException_quandoEmailJaExiste() {
        when(userRepository.existsByEmail("maria@email.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(registerRequest))
        .isInstanceOf(EmailAlreadyExistsException.class);

        verify(userRepository, never()).save(any(User.class));

    }

    @Test
    @DisplayName("deve salvar usuário com senha codificada e role USER")
    void register_deveSalvarUsuario_quandoEmailNaoExiste() {
        when(userRepository.existsByEmail("maria@email.com")).thenReturn(false);
        when(passwordEncoder.encode(any(String.class))).thenReturn("senha_codificada");
        when(jwtService.generateToken(any(User.class))).thenReturn("token_fake");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        authService.register(registerRequest);

        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();

        assertThat(savedUser.getName()).isEqualTo("Maria");
        assertThat(savedUser.getEmail()).isEqualTo("maria@email.com");
        assertThat(savedUser.getPassword()).isEqualTo("senha_codificada");
        assertThat(savedUser.getRole()).isEqualTo(Role.USER);

        verify(passwordEncoder, times(1)).encode("123456");

    }

    @Test
    @DisplayName("Deve lançar BadCredentialsException quando credenciais são inválidas")
    void login_deveLancarUsernameNotFoundException_quandoUsuarioNaoExiste() {
        doThrow(new BadCredentialsException("Credenciais inválidas"))
                .when(authenticationManager)
                .authenticate(any());

        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(BadCredentialsException.class);

        verify(authenticationManager, times(1)).authenticate(any());
        verify(userRepository, never()).findByEmail(any());
        verify(jwtService, never()).generateToken(any());

    }
    @Test
    @DisplayName("Deve retornar AuthResponse com token quando login é válido")
    void login_deveGerarToken_quandoUsuarioExiste() {
        when(userRepository.findByEmail("joao@email.com")).thenReturn(Optional.of(user));
        when(jwtService.generateToken(any(User.class))).thenReturn("token_fake");

        AuthResponse result = authService.login(loginRequest);

        assertThat(result)
                .isNotNull()
                .satisfies( r -> {
                    assertThat(r.token()).isEqualTo("token_fake");
                    assertThat(r.email()).isEqualTo("joao@email.com");
                    assertThat(r.role()).isEqualTo(Role.USER.name());
                });

        verify(authenticationManager, times(1)).authenticate(any());
        verify(jwtService, times(1)).generateToken(user);
    }

}
