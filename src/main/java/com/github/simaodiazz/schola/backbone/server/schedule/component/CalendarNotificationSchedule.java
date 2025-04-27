package com.github.simaodiazz.schola.backbone.server.schedule.component;

import com.github.simaodiazz.schola.backbone.server.calendar.data.model.Eventus;
import com.github.simaodiazz.schola.backbone.server.calendar.data.service.CalendarService;
import com.github.simaodiazz.schola.backbone.server.mail.data.model.Courier;
import com.github.simaodiazz.schola.backbone.server.mail.data.service.CourierService;
import com.github.simaodiazz.schola.backbone.server.security.data.model.User;
import org.jetbrains.annotations.NotNull;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Component
public class CalendarNotificationSchedule {

    private final @NotNull CalendarService calendarService;
    private final @NotNull CourierService courierService;

    public CalendarNotificationSchedule(@NotNull CalendarService calendarService, @NotNull CourierService courierService) {
        this.calendarService = calendarService;
        this.courierService = courierService;
    }

    @Scheduled(fixedRate = 300000)
    public void checkEventsForHalfHourRemaining() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime thirtyMinutesBeforeEnd = now.plusMinutes(30);

        List<Eventus> events = calendarService.getAllEvents();

        for (Eventus event : events) {
            if (event.getEnd().isBefore(thirtyMinutesBeforeEnd) && event.getEnd().isAfter(now)) {
                executeAction(event);
            }
        }
    }

    private void executeAction(final @NotNull Eventus eventus) {
        final User user = eventus.getUser();
        final Courier courier = courierService.user(user);
        courierService.sendMessage(eventus.getName() + " está prestes a acabar.", courier, Collections.singletonList(courier));
    }
}
