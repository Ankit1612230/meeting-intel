package com.meetingintel.meeting_intel.dto;

import com.meetingintel.meeting_intel.entity.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class AdminUserResponse {

    private Long id;
    private String fullName;
    private String email;
    private UserRole role;
    private Boolean isApproved;
    private LocalDateTime createdAt;
}