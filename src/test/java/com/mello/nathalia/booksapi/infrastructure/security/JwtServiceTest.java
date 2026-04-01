package com.mello.nathalia.booksapi.infrastructure.security;

import com.mello.nathalia.booksapi.domain.model.Role;
import com.mello.nathalia.booksapi.domain.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    private JwtService jwtService;
    private User user;

    private static final String SECRET =
            "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";

    private static final Long EXPIRATION = 86400000L;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secret", SECRET);
        ReflectionTestUtils.setField(jwtService, "expiration", EXPIRATION);

        user = new User();
        user.setEmail("joao@email.com");
        user.setName("João");
        user.setRole(Role.USER);
    }

    @Test
    @DisplayName("deve gerar token com email no subject")
    void generateToken_deveGerarToken_comEmailNoSubject() {
        String token = jwtService.generateToken(user);

        assertThat(token).isNotNull().isNotBlank();

        String email = jwtService.extractEmail(token);
        assertThat(email).isEqualTo("joao@email.com");
    }

    @Test
    @DisplayName("deve extrair email do token")
    void extractEmail_deveRetornarEmail_quandoTokenEstiverFormatadoCorretamente() {
        String token = jwtService.generateToken(user);
        String email = jwtService.extractEmail(token);

        assertThat(email).isEqualTo("joao@email.com");
    }

    @Test
    @DisplayName("deve retornar true quando token não está expirado")
    void isValid__deveRetornarTrue_quandoTokenNaoExpirado() {
        String token = jwtService.generateToken(user);

        boolean result = jwtService.isValid(token, user);

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("deve retornar false quando token está expirado")
    void isValid_deveRetornarFalse_quandoTokenExpirado() throws InterruptedException {
        ReflectionTestUtils.setField(jwtService, "expiration", 1L);

        String token = jwtService.generateToken(user);
        Thread.sleep(10);

        boolean result = jwtService.isValid(token, user);

        assertThat(result).isFalse();

        // restaura o valor original para não afetar outros testes
        ReflectionTestUtils.setField(jwtService, "expiration", EXPIRATION);
    }

    @Test
    @DisplayName("deve retornar false quando email do token não bate com o usuário")
    void isValid_deveRetornarFalse_quandoEmailNaoBate() {
        String token = jwtService.generateToken(user);

        User outroUsuario = new User();
        outroUsuario.setEmail("outro@email.com");
        outroUsuario.setName("Outro");
        outroUsuario.setRole(Role.USER);

        boolean result = jwtService.isValid(token, outroUsuario);

        assertThat(result).isFalse();
    }
}
