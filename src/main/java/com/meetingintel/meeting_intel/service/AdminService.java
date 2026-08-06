package com.meetingintel.meeting_intel.service;

import com.meetingintel.meeting_intel.entity.User;
import com.meetingintel.meeting_intel.entity.UserRole;
import com.meetingintel.meeting_intel.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;

    public List<User> getPendingUsers() {
        return userRepository.findByIsApproved(false);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User approveUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setIsApproved(true);
        return userRepository.save(user);
    }

    public User rejectUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        userRepository.delete(user);
        return user;
    }

    public User makeAdmin(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setRole(UserRole.ADMIN);
        return userRepository.save(user);
    }
}