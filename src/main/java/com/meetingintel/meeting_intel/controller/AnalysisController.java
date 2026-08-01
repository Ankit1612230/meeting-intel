package com.meetingintel.meeting_intel.controller;

import com.meetingintel.meeting_intel.entity.ActionItem;
import com.meetingintel.meeting_intel.entity.MeetingInsight;
import com.meetingintel.meeting_intel.repository.ActionItemRepository;
import com.meetingintel.meeting_intel.service.AnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

@RestController
@RequestMapping("/api/meetings")
@RequiredArgsConstructor
public class AnalysisController {

    private final AnalysisService analysisService;
    private final ActionItemRepository actionItemRepository;

    @PostMapping("/{id}/analyze")
    public ResponseEntity<MeetingInsight> analyzeMeeting(@PathVariable Long id) {
        return ResponseEntity.ok(analysisService.analyzeMeeting(id));
    }

    @GetMapping("/{id}/insights")
    public ResponseEntity<MeetingInsight> getInsights(@PathVariable Long id) {
        return ResponseEntity.ok(
                analysisService.getMeetingInsight(id));
    }

    @GetMapping("/{id}/action-items")
    public ResponseEntity<List<ActionItem>> getActionItems(@PathVariable Long id) {
        return ResponseEntity.ok(
                actionItemRepository.findByMeetingId(id));
    }
    @PatchMapping("/{meetingId}/action-items/{itemId}/complete")
    public ResponseEntity<ActionItem> markAsComplete(
            @PathVariable Long meetingId,
            @PathVariable Long itemId) {
        return ResponseEntity.ok(analysisService.markActionItemComplete(itemId));
    }

    @GetMapping("/action-items/my-pending")
    public ResponseEntity<List<ActionItem>> myPendingItems(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                analysisService.getPendingItemsByEmail(userDetails.getUsername()));
    }
}