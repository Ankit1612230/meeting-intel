package com.meetingintel.meeting_intel.controller;


import com.meetingintel.meeting_intel.dto.MeetingRequest;
import com.meetingintel.meeting_intel.dto.MeetingResponse;
import com.meetingintel.meeting_intel.service.MeetingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.Map;

@RestController
@RequestMapping("/api/meetings")
@RequiredArgsConstructor
public class MeetingController {

    private final MeetingService meetingService;
    @GetMapping("/search")
    public ResponseEntity<List<MeetingResponse>> searchMeetings(
            @RequestParam String keyword,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                meetingService.searchMeetings(keyword, userDetails.getUsername()));
    }

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboard(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                meetingService.getDashboard(userDetails.getUsername()));
    }

    @PostMapping
    public ResponseEntity<MeetingResponse> createMeeting(
            @Valid @RequestBody MeetingRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(meetingService.createMeeting(request, userDetails.getUsername()));
    }

    @GetMapping
    public ResponseEntity<List<MeetingResponse>> getAllMeetings(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity
                .ok(meetingService.getAllMeetings(userDetails.getUsername()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MeetingResponse> getMeetingById(@PathVariable Long id) {
        return ResponseEntity.ok(meetingService.getMeetingById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMeeting(@PathVariable Long id) {
        meetingService.deleteMeeting(id);
        return ResponseEntity.noContent().build();
    }
}
