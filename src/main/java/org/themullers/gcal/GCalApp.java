package org.themullers.gcal;


import org.themullers.gcal.cli.GCalCommand;
import org.themullers.gcal.google.GoogleCalendarAPI;

import java.io.IOException;
import java.security.GeneralSecurityException;

/**
 * A command-line app to interact with google calendar.  This app uses a Google service account,
 * which must be created and then granted access to the calendars that the user wishes to manipulate.
 *
 * This application uses the picocli library to parse the command line as well as Google's java
 * APIs for OAuth authentication and interacting with Google Calendar's RESTful API.
 */
public class GCalApp
{
    /**
     * Main entry point for the application.
     *
     * @param args  parameters and options the affect the behavior of the application
     */
    public static void main( String[] args ) {
        System.exit(GCalCommand.execute(args));
    }

    /**
     * List all of the calendars to which the service account is subscribed.
     */
    public static void calendars() throws GeneralSecurityException, IOException {
        for (var cal : GoogleCalendarAPI.instance().calendars()) {
            System.out.println(cal);
        }
    }

    /**
     * List upcoming events for a calendar.
     * @param calendarId  the ID of the calendar whose events to list
     * @param maxEntries  the maximum number of entries to specify
     */
    public static void upcoming(String calendarId, int maxEntries) {
        for (var eventInfo : GoogleCalendarAPI.instance().upcoming(calendarId, maxEntries)) {
            System.out.printf("%s (%s)\n", eventInfo.name(), eventInfo.start());
        }
    }

    /**
     * Subscribe to a calendar.
     * @param calendarId  the ID of the calendar to subscribe to
     */
    public static void subscribe(String calendarId) {
        GoogleCalendarAPI.instance().subscribe(calendarId);
        System.out.println("subscribed to " + calendarId);
    }
}
