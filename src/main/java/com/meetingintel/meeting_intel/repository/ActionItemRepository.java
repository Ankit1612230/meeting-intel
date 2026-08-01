package com.meetingintel.meeting_intel.repository;



import com.meetingintel.meeting_intel.entity.ActionItem;
import com.meetingintel.meeting_intel.entity.ActionItemStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface ActionItemRepository extends JpaRepository<ActionItem, Long> {
    List<ActionItem> findByMeetingId(Long meetingId);
    List<ActionItem> findByOwnerEmailAndStatus(String email, ActionItemStatus status);
    List<ActionItem> findByDueDateAndStatus(LocalDate dueDate, ActionItemStatus status);
    void deleteByMeetingId(Long meetingId);
}