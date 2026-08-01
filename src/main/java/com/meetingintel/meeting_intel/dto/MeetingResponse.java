package com.meetingintel.meeting_intel.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class MeetingResponse {
    private Long id;
    private String title;
    private LocalDateTime meetingDate;
    private List<String> participants;
    private String status;
    private LocalDateTime createdAt;
}