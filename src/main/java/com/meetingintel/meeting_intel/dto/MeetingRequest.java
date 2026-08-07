package com.meetingintel.meeting_intel.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class MeetingRequest {

    @NotBlank(message = "Title is required")
    private String title;

    @NotNull(message = "Meeting date is required")
    private LocalDateTime meetingDate;

    private List<String> participants;

    private List<String> participantNames;

    private String transcript;
}