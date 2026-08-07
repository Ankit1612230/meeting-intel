package com.meetingintel.meeting_intel.service;

import com.meetingintel.meeting_intel.dto.AdminUserResponse;
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

    public List<AdminUserResponse> getPendingUsers() {
        return userRepository.findByIsApproved(false)
                .stream()
                .map(user -> new AdminUserResponse(
                        user.getId(),
                        user.getFullName(),
                        user.getEmail(),
                        user.getRole(),
                        user.getIsApproved(),
                        user.getCreatedAt()
                ))
                .toList();
    }

    public List<AdminUserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(user -> new AdminUserResponse(
                        user.getId(),
                        user.getFullName(),
                        user.getEmail(),
                        user.getRole(),
                        user.getIsApproved(),
                        user.getCreatedAt()
                ))
                .toList();
    }

    public AdminUserResponse approveUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setIsApproved(true);
        User saved = userRepository.save(user);
        return new AdminUserResponse(
                saved.getId(),
                saved.getFullName(),
                saved.getEmail(),
                saved.getRole(),
                saved.getIsApproved(),
                saved.getCreatedAt()
        );
    }

    public AdminUserResponse rejectUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        userRepository.delete(user);
        return new AdminUserResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getRole(),
                user.getIsApproved(),
                user.getCreatedAt()
        );
    }

    public AdminUserResponse makeAdmin(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setRole(UserRole.ADMIN);
        User saved = userRepository.save(user);
        return new AdminUserResponse(
                saved.getId(),
                saved.getFullName(),
                saved.getEmail(),
                saved.getRole(),
                saved.getIsApproved(),
                saved.getCreatedAt()
        );
    }
}