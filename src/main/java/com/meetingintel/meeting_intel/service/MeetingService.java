package com.meetingintel.meeting_intel.service;

import lombok.RequiredArgsConstructor;
import com.meetingintel.meeting_intel.dto.MeetingRequest;
import com.meetingintel.meeting_intel.dto.MeetingResponse;
import com.meetingintel.meeting_intel.entity.Meeting;
import com.meetingintel.meeting_intel.entity.User;
import com.meetingintel.meeting_intel.repository.MeetingRepository;
import com.meetingintel.meeting_intel.repository.UserRepository;
import org.springframework.stereotype.Service;
import com.meetingintel.meeting_intel.entity.ActionItem;
import com.meetingintel.meeting_intel.entity.ActionItemStatus;
import com.meetingintel.meeting_intel.entity.MeetingStatus;
import com.meetingintel.meeting_intel.repository.ActionItemRepository;
import java.time.LocalDate;
import java.util.Map;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MeetingService {

    private final MeetingRepository meetingRepository;
    private final UserRepository userRepository;
    private final ActionItemRepository actionItemRepository;

    public MeetingResponse createMeeting(MeetingRequest request, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Meeting meeting = new Meeting();
        meeting.setTitle(request.getTitle());
        meeting.setMeetingDate(request.getMeetingDate());
        meeting.setParticipants(request.getParticipants());
        meeting.setTranscript(request.getTranscript());
        meeting.setCreatedBy(user);

        Meeting saved = meetingRepository.save(meeting);
        return mapToResponse(saved);
    }

    public List<MeetingResponse> getAllMeetings(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return meetingRepository.findByCreatedById(user.getId())
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public MeetingResponse getMeetingById(Long id) {
        Meeting meeting = meetingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Meeting not found"));
        return mapToResponse(meeting);
    }

    public void deleteMeeting(Long id) {
        meetingRepository.deleteById(id);
    }

    private MeetingResponse mapToResponse(Meeting meeting) {
        MeetingResponse response = new MeetingResponse();
        response.setId(meeting.getId());
        response.setTitle(meeting.getTitle());
        response.setMeetingDate(meeting.getMeetingDate());
        response.setParticipants(meeting.getParticipants());
        response.setStatus(meeting.getStatus().name());
        response.setCreatedAt(meeting.getCreatedAt());
        return response;
    }
    public List<MeetingResponse> searchMeetings(String keyword, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return meetingRepository.findByCreatedById(user.getId())
                .stream()
                .filter(meeting -> meeting.getTitle()
                        .toLowerCase()
                        .contains(keyword.toLowerCase())
                        || (meeting.getTranscript() != null &&
                        meeting.getTranscript()
                                .toLowerCase()
                                .contains(keyword.toLowerCase())))
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public Map<String, Object> getDashboard(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Meeting> allMeetings = meetingRepository.findByCreatedById(user.getId());

        long totalMeetings = allMeetings.size();
        long analyzedMeetings = allMeetings.stream()
                .filter(m -> m.getStatus() == MeetingStatus.ANALYZED)
                .count();

        List<ActionItem> allActionItems = allMeetings.stream()
                .flatMap(m -> actionItemRepository.findByMeetingId(m.getId()).stream())
                .collect(Collectors.toList());

        long pendingItems = allActionItems.stream()
                .filter(a -> a.getStatus() == ActionItemStatus.PENDING)
                .count();

        long overdueItems = allActionItems.stream()
                .filter(a -> a.getStatus() == ActionItemStatus.PENDING
                        && a.getDueDate() != null
                        && a.getDueDate().isBefore(LocalDate.now()))
                .count();

        return Map.of(
                "totalMeetings", totalMeetings,
                "analyzedMeetings", analyzedMeetings,
                "pendingActionItems", pendingItems,
                "overdueActionItems", overdueItems
        );
    }
}