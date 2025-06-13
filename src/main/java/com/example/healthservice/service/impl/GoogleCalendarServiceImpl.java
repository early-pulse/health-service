package com.example.healthservice.service.impl;

import com.example.healthservice.config.GoogleCalendarConfig;
import com.example.healthservice.model.Appointment;
import com.example.healthservice.service.GoogleCalendarService;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.EventReminder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.util.Arrays;

@Service
@Slf4j
@RequiredArgsConstructor
public class GoogleCalendarServiceImpl implements GoogleCalendarService {

    private final Calendar googleCalendar;
    private static final String CALENDAR_ID = "primary";

    @Override
    public void createEvent(Appointment appointment) {
        try {
            Event event = createEventDetails(appointment);
            event = googleCalendar.events().insert(CALENDAR_ID, event)
                    .setSendUpdates("none")
                    .execute();
            log.info("Event created: {}", event.getHtmlLink());
        } catch (Exception e) {
            log.error("Error creating calendar event for appointment: {}", appointment.getId(), e);
        }
    }

    @Override
    public void updateEvent(Appointment appointment) {
        try {
            String eventId = getEventId(appointment.getId());
            if (eventId != null) {
                Event event = createEventDetails(appointment);
                event = googleCalendar.events().update(CALENDAR_ID, eventId, event)
                        .setSendUpdates("none")
                        .execute();
                log.info("Event updated: {}", event.getHtmlLink());
            }
        } catch (Exception e) {
            log.error("Error updating calendar event for appointment: {}", appointment.getId(), e);
        }
    }

    @Override
    public void deleteEvent(String eventId) {
        try {
            googleCalendar.events().delete(CALENDAR_ID, eventId)
                    .setSendUpdates("all")
                    .execute();
            log.info("Event deleted: {}", eventId);
        } catch (Exception e) {
            log.error("Error deleting calendar event: {}", eventId, e);
        }
    }

    @Override
    public String getEventId(String appointmentId) {
        // TODO: Implement event ID storage and retrieval
        // This could be stored in the Appointment entity or a separate mapping table
        return null;
    }

    @Override
    public String getEventLink(String appointmentId) {
        try {
            String eventId = getEventId(appointmentId);
            if (eventId != null) {
                return "https://calendar.google.com/calendar/event?eid=" + eventId;
            }
        } catch (Exception e) {
            log.error("Error getting calendar event link for appointment: {}", appointmentId, e);
        }
        return null;
    }

    private Event createEventDetails(Appointment appointment) {
        String summary = appointment.getIsLab() 
            ? String.format("Lab Test: %s", appointment.getTestType())
            : "Doctor Appointment";
            
        Event event = new Event()
                .setSummary(summary)
                .setLocation("Health Service Facility")
                .setDescription(String.format("Appointment with %s", appointment.getEntityName()));

        // Set start time
        EventDateTime start = new EventDateTime()
                .setDateTime(new com.google.api.client.util.DateTime(
                        appointment.getAppointmentDateTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()))
                .setTimeZone(ZoneId.systemDefault().getId());
        event.setStart(start);

        // Set end time (1 hour after start)
        EventDateTime end = new EventDateTime()
                .setDateTime(new com.google.api.client.util.DateTime(
                        appointment.getAppointmentDateTime().plusHours(1).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()))
                .setTimeZone(ZoneId.systemDefault().getId());
        event.setEnd(end);

        // Add attendees without sending updates
        EventAttendee[] attendees = new EventAttendee[] {
                new EventAttendee().setEmail(appointment.getUserEmail()),
                new EventAttendee().setEmail(appointment.getEntityEmail())
        };
        event.setAttendees(Arrays.asList(attendees));

        // Add reminders
        EventReminder[] reminderOverrides = new EventReminder[] {
                new EventReminder().setMethod("email").setMinutes(24 * 60),
                new EventReminder().setMethod("popup").setMinutes(30)
        };
        Event.Reminders reminders = new Event.Reminders()
                .setUseDefault(false)
                .setOverrides(Arrays.asList(reminderOverrides));
        event.setReminders(reminders);

        return event;
    }
} 