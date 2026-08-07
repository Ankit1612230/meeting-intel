package com.meetingintel.meeting_intel.controller;
import com.meetingintel.meeting_intel.dto.AdminUserResponse;
import com.meetingintel.meeting_intel.entity.User;
import com.meetingintel.meeting_intel.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/pending")
    public List<AdminUserResponse> getPendingUsers() {
        return adminService.getPendingUsers();
    }

    @GetMapping("/users")
    public List<AdminUserResponse> getAllUsers() {
        return adminService.getAllUsers();
    }

    @PutMapping("/approve/{id}")
    public AdminUserResponse approveUser(@PathVariable Long id) {
        return adminService.approveUser(id);
    }

    @DeleteMapping("/reject/{id}")
    public AdminUserResponse rejectUser(@PathVariable Long id) {
        return adminService.rejectUser(id);
    }

    @PutMapping("/make-admin/{id}")
    public AdminUserResponse makeAdmin(@PathVariable Long id) {
        return adminService.makeAdmin(id);
    }
}