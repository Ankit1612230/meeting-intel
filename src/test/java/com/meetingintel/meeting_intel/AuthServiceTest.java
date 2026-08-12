package com.meetingintel.meeting_intel;

import com.meetingintel.meeting_intel.dto.RegisterRequest;
import com.meetingintel.meeting_intel.entity.User;
import com.meetingintel.meeting_intel.repository.UserRepository;
import com.meetingintel.meeting_intel.security.JwtUtil;
import com.meetingintel.meeting_intel.service.AuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

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

    @Test
    void register_ShouldReturnMessage_WhenNewUser() {
        RegisterRequest request = new RegisterRequest();
        request.setFullName("Test User");
        request.setEmail("test@gmail.com");
        request.setPassword("test123");

        when(userRepository.existsByEmail("test@gmail.com")).thenReturn(false);
        when(passwordEncoder.encode("test123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(new User());

        Map<String, String> result = authService.register(request);

        assertEquals("Registration successful. Please wait for admin approval before logging in.",
                result.get("message"));
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void register_ShouldThrowException_WhenEmailAlreadyExists() {
        RegisterRequest request = new RegisterRequest();
        request.setFullName("Test User");
        request.setEmail("existing@gmail.com");
        request.setPassword("test123");

        when(userRepository.existsByEmail("existing@gmail.com")).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> authService.register(request));

        assertEquals("Email already registered", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }
}