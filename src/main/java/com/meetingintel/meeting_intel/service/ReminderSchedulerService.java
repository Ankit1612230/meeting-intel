package com.meetingintel.meeting_intel.service;

import com.meetingintel.meeting_intel.entity.ActionItem;
import com.meetingintel.meeting_intel.entity.ActionItemStatus;
import com.meetingintel.meeting_intel.repository.ActionItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReminderSchedulerService {

    private final ActionItemRepository actionItemRepository;
    private final EmailService emailService;

    @Scheduled(cron = "0 0 9 * * *")
    public void sendDailyReminders() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        List<ActionItem> dueTomorrow = actionItemRepository
                .findByDueDateAndStatus(tomorrow, ActionItemStatus.PENDING);

        System.out.println("Scheduler running - found "
                + dueTomorrow.size() + " action items due tomorrow");

        for (ActionItem item : dueTomorrow) {
            try {
                emailService.sendActionItemReminder(
                        item.getOwnerEmail(),
                        item.getTask(),
                        item.getDueDate().toString()
                );
                System.out.println("Reminder sent to: " + item.getOwnerEmail());
            } catch (Exception e) {
                System.out.println("Failed to send reminder to "
                        + item.getOwnerEmail() + ": " + e.getMessage());
            }
        }
    }
}
//make this changes to recieve email at 9am daily
//@Scheduled(cron = "0 */2 * * * *") to @Scheduled(cron = "0 0 9 * * *")
//LocalDate tomorrow = LocalDate.parse("2026-07-29"); to LocalDate tomorrow = LocalDate.now().plusDays(1);