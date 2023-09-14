package org.themullers.gcal.cli;

import org.themullers.gcal.GCalApp;
import picocli.CommandLine;

import java.util.concurrent.Callable;

@CommandLine.Command(name="upcoming", description = "list upcoming events for a calendar")
public class UpcomingSubcommand implements Callable<Integer> {

    @CommandLine.Parameters(index="0", description = "show upcoming events for this calendar")
    String calendarId;

    @CommandLine.Option(names = {"-n", "--num-events"}, description = "max number of events to show")
    int numberOfEvents = 5;

    @Override
    public Integer call() throws Exception {

        GCalApp.upcoming(calendarId, numberOfEvents);

        return 0;
    }
}
