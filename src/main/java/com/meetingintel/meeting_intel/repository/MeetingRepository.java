package com.meetingintel.meeting_intel.repository;

import  com.meetingintel.meeting_intel.entity.Meeting;
import  com.meetingintel.meeting_intel.entity.MeetingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MeetingRepository extends JpaRepository<Meeting, Long> {
    List<Meeting> findByCreatedById(Long userId);
    List<Meeting> findByStatus(MeetingStatus status);
}
