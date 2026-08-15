package com.meetingintel.meeting_intel;

import com.meetingintel.meeting_intel.entity.*;
import com.meetingintel.meeting_intel.repository.*;
import com.meetingintel.meeting_intel.service.AnalysisService;
import com.meetingintel.meeting_intel.service.GroqAiService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AnalysisServiceTest {

    @Mock
    private GroqAiService groqAiService;

    @Mock
    private MeetingRepository meetingRepository;

    @Mock
    private MeetingInsightRepository meetingInsightRepository;

    @Mock
    private ActionItemRepository actionItemRepository;

    @InjectMocks
    private AnalysisService analysisService;

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void analyzeMeeting_ShouldThrowException_WhenMeetingNotFound() {
        when(meetingRepository.findById(999L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> analysisService.analyzeMeeting(999L));

        assertEquals("Meeting not found", exception.getMessage());
    }

    @Test
    void analyzeMeeting_ShouldThrowException_WhenTranscriptIsEmpty() {
        Meeting meeting = new Meeting();
        meeting.setId(1L);
        meeting.setTranscript(null);

        when(meetingRepository.findById(1L)).thenReturn(Optional.of(meeting));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> analysisService.analyzeMeeting(1L));

        assertEquals("No transcript found for this meeting", exception.getMessage());
    }

    @Test
    void analyzeMeeting_ShouldSaveInsight_WhenValidTranscript() {
        Meeting meeting = new Meeting();
        meeting.setId(1L);
        meeting.setTranscript("Alice will prepare report by 2026-08-10");
        meeting.setParticipants(List.of("alice@gmail.com"));
        meeting.setParticipantNames(List.of("Alice"));
        meeting.setStatus(MeetingStatus.PENDING);

        String aiResponse = "{\"summary\": \"Test summary\", " +
                "\"decisions\": [\"Decision 1\"], " +
                "\"actionItems\": [{\"task\": \"Prepare report\", " +
                "\"owner\": \"Alice\", \"dueDate\": \"2026-08-10\"}]}";

        MeetingInsight savedInsight = new MeetingInsight();
        savedInsight.setId(1L);
        savedInsight.setSummary("Test summary");
        savedInsight.setAnalyzedAt(LocalDateTime.now());

        when(meetingRepository.findById(1L)).thenReturn(Optional.of(meeting));
        when(groqAiService.analyzeTranscript(any())).thenReturn(aiResponse);
        when(meetingInsightRepository.findByMeetingId(1L)).thenReturn(Optional.empty());
        when(meetingInsightRepository.save(any())).thenReturn(savedInsight);
        when(meetingRepository.save(any())).thenReturn(meeting);

        MeetingInsight result = analysisService.analyzeMeeting(1L);

        assertNotNull(result);
        verify(groqAiService, times(1)).analyzeTranscript(any());
        verify(actionItemRepository, times(1)).saveAll(any());
        verify(meetingInsightRepository, times(1)).save(any());
    }
}