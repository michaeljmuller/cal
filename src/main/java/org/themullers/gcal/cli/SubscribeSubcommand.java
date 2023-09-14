package org.themullers.gcal.cli;

import org.themullers.gcal.GCalApp;
import picocli.CommandLine;

import java.util.concurrent.Callable;

@CommandLine.Command(name="subscribe", description = "subscribe to a calendar")
public class SubscribeSubcommand implements Callable<Integer> {

    @CommandLine.Parameters(index="0", description = "the calendar to subscribe to")
    String calendarId;

    @Override
    public Integer call() throws Exception {
        GCalApp.subscribe(calendarId);
        return 0;
    }
}
