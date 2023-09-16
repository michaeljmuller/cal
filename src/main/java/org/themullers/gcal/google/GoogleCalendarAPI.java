package org.themullers.gcal.google;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.CalendarListEntry;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.ServiceAccountCredentials;
import org.themullers.gcal.EventInfo;
import org.themullers.gcal.util.Rethrow;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class GoogleCalendarAPI {

    Calendar service;
    String email;
    static GoogleCalendarAPI instance = null;

    /**
     * Returns a singleton instance of the GoogleCalendarAPI object
     *
     * @return a GoogleCalendarAPI object
     */
    synchronized public static GoogleCalendarAPI instance() {
        if (instance == null) {
            instance = new GoogleCalendarAPI();
        }
        return instance;
    }

    /**
     * GoogleCalendarAPI constructor.
     */
    protected GoogleCalendarAPI() {
        authenticate();
    }

    /**
     * List upcoming events from the specified calendar.
     *
     * @param calendarId  the calendar whose events to list
     * @param count  the max number of entries to return
     * @return  information about upcoming events from the specified calendar
     */
    public List<EventInfo> upcoming(String calendarId, int count) {
        return rethrow(() -> {

            var eventInfo = new LinkedList<EventInfo>();

            // get the list of events
            DateTime now = new DateTime(System.currentTimeMillis());
            Events events = service.events().list(calendarId)
                    .setMaxResults(count)
                    .setTimeMin(now)
                    .setOrderBy("startTime")
                    .setSingleEvents(true)
                    .execute();

            // extract the summary and start time from each event
            for (Event event : events.getItems()) {
                DateTime start = event.getStart().getDateTime();
                if (start == null) {
                    start = event.getStart().getDate();
                }
                eventInfo.add(new EventInfo(event.getSummary(), start));
            }

            return eventInfo;
        });
    }

    /**
     * Subscribe to a calendar.  Adds the calendar to the service account's list of calendars.
     * After someone invites the service account to view a calendar, this method must be
     * called to "accept the invitation".
     *
     * @param calendarId  the ID of the calendar to subscribe to
     */
    public void subscribe(String calendarId)  {
        rethrow(() -> {
            var calendar = new CalendarListEntry();
            calendar.setId(calendarId);
            service.calendarList().insert(calendar).execute();
        });
    }

    /**
     * List the calendars to which the service account is subscribed.
     *
     * @return  a list of calendar IDs
     */
    public List<String> calendars() {
        return rethrow(() -> {
            var calendars = new LinkedList<String>();
            for (var cal : service.calendarList().list().execute().getItems()) {
                calendars.add(cal.getSummary());
            }
            return calendars;
        });
    }

    /**
     * Get the service account's email address.  In order for this tool to be able
     * to operate on a calendar, that calendar must be shared with this email address.
     *
     * @return the email address associated with the credentials loaded at startup
     */
    public String getServiceAccountEmail() {
        return email;
    }

    public void addEvent(String calendarId) {
        rethrow(() -> {
            var dt = new DateTime("2023-09-16");
            var tz = "America/Denver";
            var edt = new EventDateTime().setDate(dt).setTimeZone(tz);

            var event = new Event();
            event.setSummary("test event");
            event.setStart(edt);
            event.setEnd(edt);

            service.events().insert(calendarId, event).execute();
        });
    }

    /**
     * Helper method to execute code, re-throwing any exceptions as a GoogleCalendarAPIException.
     * @param run  the code to execute
     */
    public void rethrow(Rethrow.ThrowingRunnable run) {
        Rethrow.exec(GoogleCalendarAPIException.class, run);
    }

    /**
     * Helper method to execute a function, re-throwing any exceptions as a GoogleCalendarAPIException.
     * @param func  the function to execute
     * @param <T>  the type of the function's return
     * @return  the value returned by the function
     */
    public <T> T rethrow(Rethrow.ThrowingSupplier<T> func) {
        return Rethrow.exec(GoogleCalendarAPIException.class, func);
    }

    /**
     * Authenticate with Google and set the service object that can be used to make Calendar API calls
     * as well as the email associated with that service account.
     */
    protected void authenticate() {

        // open the service account credential json file for reading
        try (var fis = new FileInputStream(credentialFile())) {

            // parse the service account credential file and extract the email
            var sac = ServiceAccountCredentials.fromStream(fis);
            email = sac.getClientEmail();

            // create a service object that can be used to perform calendar operations
            var transport = GoogleNetHttpTransport.newTrustedTransport();
            var jsonFactory = new GsonFactory();
            var scope = Collections.singleton(CalendarScopes.CALENDAR);
            var credentials = sac.createScoped(scope);
            var initializer = new HttpCredentialsAdapter(credentials);
            service = new Calendar.Builder(transport, jsonFactory, initializer)
                .setApplicationName("Google Calendar CLI")
                .build();
        }
        catch (IOException | GeneralSecurityException e) {
            throw new GoogleCalendarAPIException(e);
        }
    }

    /**
     * Returns a path to the service account's credentials file.  This file is assumed to be
     * in ~/.gcal/service-account-credentials.json, but the location can be overridden with
     * the GCAL_SERVICE_ACCOUNT_CREDENTIALS environment varible.
     *
     * @return  The path to the credentials file.
     * @throws FileNotFoundException  thrown if the credentials file cannot be found
     */
    protected String credentialFile() throws FileNotFoundException {

        // first see if the location was specified with an environment variable
        var credFile = System.getenv("GCAL_SERVICE_ACCOUNT_CREDENTIALS");

        // if the envar is not specified, then look in the default location
        if (credFile == null) {
            credFile = "~/.gcal/service-account-credentials.json";
        }

        // java doesn't grok the unix ~ in pathnames -- this only handles ~/foo.json, not ~user/foo.json
        credFile = credFile.replaceFirst("^~/", System.getProperty("user.home") + "/");

        // if the credentials file doesn't exist, throw an exception
        if (!(new File(credFile).exists())) {
            throw new FileNotFoundException("unable to find service account credential file " + credFile);
        }

        return credFile;
    }
}
