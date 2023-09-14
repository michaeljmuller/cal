package org.themullers.gcal.google;

/**
 * Runtime exception wrapper to simplify method signatures in this application.
 */
public class GoogleCalendarAPIException extends RuntimeException {
    public GoogleCalendarAPIException(Exception e) {
        super(e);
    }
}
