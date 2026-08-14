package com.meetingintel.meeting_intel;

import com.meetingintel.meeting_intel.dto.MeetingRequest;
import com.meetingintel.meeting_intel.dto.MeetingResponse;
import com.meetingintel.meeting_intel.entity.Meeting;
import com.meetingintel.meeting_intel.entity.MeetingStatus;
import com.meetingintel.meeting_intel.entity.User;
import com.meetingintel.meeting_intel.repository.ActionItemRepository;
import com.meetingintel.meeting_intel.repository.MeetingInsightRepository;
import com.meetingintel.meeting_intel.repository.MeetingRepository;
import com.meetingintel.meeting_intel.repository.UserRepository;
import com.meetingintel.meeting_intel.service.MeetingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MeetingServiceTest {

    @Mock
    private MeetingRepository meetingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ActionItemRepository actionItemRepository;

    @Mock
    private MeetingInsightRepository meetingInsightRepository;

    @InjectMocks
    private MeetingService meetingService;

    @Test
    void createMeeting_ShouldReturnMeetingResponse_WhenValidRequest() {
        User user = new User();
        user.setId(1L);
        user.setEmail("test@gmail.com");

        MeetingRequest request = new MeetingRequest();
        request.setTitle("Test Meeting");
        request.setMeetingDate(LocalDateTime.now());
        request.setParticipants(List.of("alice@gmail.com"));
        request.setTranscript("Alice will prepare report by 2026-08-10");

        Meeting savedMeeting = new Meeting();
        savedMeeting.setId(1L);
        savedMeeting.setTitle("Test Meeting");
        savedMeeting.setMeetingDate(request.getMeetingDate());
        savedMeeting.setParticipants(request.getParticipants());
        savedMeeting.setStatus(MeetingStatus.PENDING);
        savedMeeting.setCreatedAt(LocalDateTime.now());
        savedMeeting.setCreatedBy(user);

        when(userRepository.findByEmail("test@gmail.com")).thenReturn(Optional.of(user));
        when(meetingRepository.save(any(Meeting.class))).thenReturn(savedMeeting);

        MeetingResponse response = meetingService.createMeeting(request, "test@gmail.com");

        assertNotNull(response);
        assertEquals("Test Meeting", response.getTitle());
        assertEquals("PENDING", response.getStatus());
        verify(meetingRepository, times(1)).save(any(Meeting.class));
    }

    @Test
    void getMeetingById_ShouldThrowException_WhenMeetingNotFound() {
        when(meetingRepository.findById(999L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> meetingService.getMeetingById(999L));

        assertEquals("Meeting not found", exception.getMessage());
    }

    @Test
    void deleteMeeting_ShouldDeleteSuccessfully_WhenMeetingExists() {
        Meeting meeting = new Meeting();
        meeting.setId(1L);
        meeting.setStatus(MeetingStatus.PENDING);

        when(meetingRepository.findById(1L)).thenReturn(Optional.of(meeting));

        meetingService.deleteMeeting(1L);

        verify(actionItemRepository, times(1)).deleteByMeetingId(1L);
        verify(meetingRepository, times(1)).delete(meeting);
    }
}