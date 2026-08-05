package com.meetingintel.meeting_intel.service;

import com.meetingintel.meeting_intel.dto.RegisterRequest;
import com.meetingintel.meeting_intel.entity.User;
import com.meetingintel.meeting_intel.repository.UserRepository;
import com.meetingintel.meeting_intel.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public Map<String, String> register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);

        return Map.of("message", "Registration successful. Please wait for admin approval before logging in.");
    }

    public Map<String, String> login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.getIsApproved()) {
            throw new RuntimeException("Your account is pending approval. Please contact the admin.");
        }

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password));
        String token = jwtUtil.generateToken(email);
        return Map.of("token", token);
    }
}