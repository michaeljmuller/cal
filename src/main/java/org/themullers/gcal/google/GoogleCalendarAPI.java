package org.themullers.gcal.google;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.CalendarListEntry;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import org.themullers.gcal.EventInfo;

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
        service = authenticate();
    }

    /**
     * List upcoming events from the specified calendar.
     *
     * @param calendarId  the calendar whose events to list
     * @param count  the max number of entries to return
     * @return  information about upcoming events from the specified calendar
     */
    public List<EventInfo> upcoming(String calendarId, int count) {

        var eventInfo = new LinkedList<EventInfo>();

        try {

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

        }
        catch (IOException e) {
            throw new GoogleCalendarAPIException(e);
        }

        return eventInfo;
    }

    /**
     * Subscribe to a calendar.  Adds the calendar to the service account's list of calendars.
     * After someone invites the service account to view a calendar, this method must be
     * called to "accept the invitation".
     *
     * @param calendarId  the ID of the calendar to subscribe to
     */
    public void subscribe(String calendarId)  {
        try {
            var calendar = new CalendarListEntry();
            calendar.setId(calendarId);
            service.calendarList().insert(calendar).execute();
        }
        catch (IOException e) {
            throw new GoogleCalendarAPIException(e);
        }
    }

    /**
     * List the calendars to which the service account is subscribed.
     *
     * @return  a list of calendar IDs
     */
    public List<String> calendars() throws GeneralSecurityException, IOException {
        try {
            var calendars = new LinkedList<String>();
            for (var cal : service.calendarList().list().execute().getItems()) {
                calendars.add(cal.getSummary());
            }
            return calendars;
        }
        catch (IOException e) {
            throw new GoogleCalendarAPIException(e);
        }
    }

    /**
     * Authenticate with Google and return a service object that can be used to make Calendar API calls.
     * @return  a service object
     */
    protected Calendar authenticate() {

        try (var fis = new FileInputStream(credentialFile())) {

            GoogleCredentials credentials = ServiceAccountCredentials.fromStream(fis)
                    .createScoped(Collections.singleton(CalendarScopes.CALENDAR));

            Calendar service = new Calendar.Builder(GoogleNetHttpTransport.newTrustedTransport(), new GsonFactory(), new HttpCredentialsAdapter(credentials))
                    .setApplicationName("Test")
                    .build();

            return service;
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
