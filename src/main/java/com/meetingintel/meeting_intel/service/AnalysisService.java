package com.meetingintel.meeting_intel.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.meetingintel.meeting_intel.entity.ActionItem;
import com.meetingintel.meeting_intel.entity.ActionItemStatus;
import com.meetingintel.meeting_intel.entity.Meeting;
import com.meetingintel.meeting_intel.entity.MeetingInsight;
import com.meetingintel.meeting_intel.entity.MeetingStatus;
import com.meetingintel.meeting_intel.repository.ActionItemRepository;
import com.meetingintel.meeting_intel.repository.MeetingInsightRepository;
import com.meetingintel.meeting_intel.repository.MeetingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AnalysisService {

    private final GroqAiService groqAiService;
    private final MeetingRepository meetingRepository;
    private final MeetingInsightRepository meetingInsightRepository;
    private final ActionItemRepository actionItemRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public MeetingInsight analyzeMeeting(Long meetingId) {
        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new RuntimeException("Meeting not found"));

        if (meeting.getTranscript() == null || meeting.getTranscript().isEmpty()) {
            throw new RuntimeException("No transcript found for this meeting");
        }

        String aiResponse = groqAiService.analyzeTranscript(meeting.getTranscript());

        MeetingInsight insight = meetingInsightRepository
                .findByMeetingId(meetingId)
                .orElse(new MeetingInsight());

        insight.setMeeting(meeting);
        insight.setRawAiResponse(aiResponse);

        try {
            String cleanedResponse = aiResponse.trim();
            if (cleanedResponse.contains("```json")) {
                cleanedResponse = cleanedResponse
                        .substring(cleanedResponse.indexOf("```json") + 7);
                cleanedResponse = cleanedResponse
                        .substring(0, cleanedResponse.lastIndexOf("```"));
            }

            JsonNode root = objectMapper.readTree(cleanedResponse.trim());

            insight.setSummary(root.path("summary").asText());
            insight.setDecisions(root.path("decisions").toString());

            actionItemRepository.deleteByMeetingId(meetingId);
            List<ActionItem> actionItems = new ArrayList<>();
            JsonNode items = root.path("actionItems");

            for (JsonNode item : items) {
                System.out.println("SAVING ACTION ITEM: " + item.toString());
                ActionItem actionItem = new ActionItem();
                actionItem.setTask(item.path("task").asText());

                String owner = item.path("owner").asText();
                String ownerEmail = resolveOwnerEmail(
                        owner,
                        meeting.getParticipants(),
                        meeting.getParticipantNames()
                );
                actionItem.setOwnerEmail(ownerEmail);

                String dueDateStr = item.path("dueDate").asText();
                if (dueDateStr != null && !dueDateStr.equals("null")
                        && !dueDateStr.isEmpty()) {
                    try {
                        actionItem.setDueDate(LocalDate.parse(dueDateStr));
                    } catch (Exception e) {
                        actionItem.setDueDate(LocalDate.now().plusDays(1));
                    }
                }
                actionItem.setMeeting(meeting);
                actionItems.add(actionItem);
            }

            actionItemRepository.saveAll(actionItems);

        } catch (Exception e) {
            System.out.println("PARSING ERROR: " + e.getMessage());
            insight.setSummary("AI response parsing failed: " + e.getMessage());
        }

        meeting.setStatus(MeetingStatus.ANALYZED);
        meetingRepository.save(meeting);

        return meetingInsightRepository.save(insight);
    }

    public MeetingInsight getMeetingInsight(Long meetingId) {
        return meetingInsightRepository.findByMeetingId(meetingId)
                .orElseThrow(() -> new RuntimeException("No insights found for this meeting"));
    }

    public ActionItem markActionItemComplete(Long itemId) {
        ActionItem item = actionItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Action item not found"));
        item.setStatus(ActionItemStatus.DONE);
        item.setCompletedAt(java.time.LocalDateTime.now());
        return actionItemRepository.save(item);
    }

    public List<ActionItem> getPendingItemsByEmail(String email) {
        return actionItemRepository.findByOwnerEmailAndStatus(
                email, ActionItemStatus.PENDING);
    }

    private String resolveOwnerEmail(String owner, List<String> participants,
                                     List<String> participantNames) {
        if (owner == null || owner.isEmpty()) {
            return participants != null && !participants.isEmpty()
                    ? participants.get(0) : owner;
        }

        // If owner is already an email
        if (owner.contains("@")) {
            return owner;
        }

        // Match by participant names if provided
        if (participantNames != null && participants != null) {
            for (int i = 0; i < participantNames.size(); i++) {
                String name = participantNames.get(i).toLowerCase();
                String ownerLower = owner.toLowerCase();

                if (name.contains(ownerLower) || ownerLower.contains(name)
                        || name.startsWith(ownerLower) || ownerLower.startsWith(name)) {
                    if (i < participants.size()) {
                        return participants.get(i);
                    }
                }
            }
        }

        // Match by email prefix
        if (participants != null) {
            for (String participant : participants) {
                String emailPrefix = participant
                        .substring(0, participant.indexOf("@"))
                        .toLowerCase()
                        .replace(".", " ")
                        .replace("_", " ");

                if (emailPrefix.contains(owner.toLowerCase())
                        || owner.toLowerCase().contains(emailPrefix)) {
                    return participant;
                }
            }
            return participants.get(0);
        }

        return owner;
    }
}