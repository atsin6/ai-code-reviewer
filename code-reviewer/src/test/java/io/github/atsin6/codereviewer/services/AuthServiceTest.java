package io.github.atsin6.codereviewer.services;

import io.github.atsin6.codereviewer.model.dto.request.AuthRequest;
import io.github.atsin6.codereviewer.model.dto.response.AuthResponse;
import io.github.atsin6.codereviewer.model.entity.User;
import io.github.atsin6.codereviewer.exceptions.EmailAlreadyExistsException;
import io.github.atsin6.codereviewer.repositories.UserRepository;
import io.github.atsin6.codereviewer.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    private AuthRequest authRequest;

    @BeforeEach
    void setUp() {
        authRequest = new AuthRequest("test@example.com", "password123");
    }

    @Test
    void shouldRegisterSuccessfully() {
        when(userRepository.findByEmail(authRequest.email())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(authRequest.password())).thenReturn("hashed_pw");
        when(jwtUtil.generateToken(authRequest.email())).thenReturn("jwt_token");

        AuthResponse response = authService.register(authRequest);

        assertThat(response).isNotNull();
        assertThat(response.email()).isEqualTo("test@example.com");
        assertThat(response.token()).isEqualTo("jwt_token");

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();
        assertThat(savedUser.getEmail()).isEqualTo("test@example.com");
        assertThat(savedUser.getPasswordHash()).isEqualTo("hashed_pw");
    }

    @Test
    void shouldThrowWhenRegisteringDuplicateEmail() {
        when(userRepository.findByEmail(authRequest.email())).thenReturn(Optional.of(new User()));

        assertThrows(EmailAlreadyExistsException.class, () -> authService.register(authRequest));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldLoginSuccessfully() {
        User mockUser = User.builder().email("test@example.com").passwordHash("hashed_pw").build();
        
        when(userRepository.findByEmail(authRequest.email())).thenReturn(Optional.of(mockUser));
        when(jwtUtil.generateToken(authRequest.email())).thenReturn("jwt_token");

        AuthResponse response = authService.login(authRequest);

        assertThat(response).isNotNull();
        assertThat(response.email()).isEqualTo("test@example.com");
        assertThat(response.token()).isEqualTo("jwt_token");
        
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void shouldPropagateBadCredentialsExceptionOnLogin() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThrows(BadCredentialsException.class, () -> authService.login(authRequest));
    }
}
