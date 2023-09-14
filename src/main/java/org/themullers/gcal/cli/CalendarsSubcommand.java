package org.themullers.gcal.cli;

import org.themullers.gcal.GCalApp;
import picocli.CommandLine;

import java.util.concurrent.Callable;

@CommandLine.Command(name="calendars", description = "list calendars available to this tool")
public class CalendarsSubcommand implements Callable<Integer> {

    @Override
    public Integer call() throws Exception {

        GCalApp.calendars();

        return 0;
    }
}
