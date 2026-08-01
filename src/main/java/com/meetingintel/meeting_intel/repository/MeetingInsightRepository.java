package com.meetingintel.meeting_intel.repository;

import com.meetingintel.meeting_intel.entity.MeetingInsight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface MeetingInsightRepository extends JpaRepository<MeetingInsight, Long> {
    Optional<MeetingInsight> findByMeetingId(Long meetingId);
}