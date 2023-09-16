package org.themullers.gcal.cli;

import org.themullers.gcal.GCalApp;
import picocli.CommandLine;

import java.util.concurrent.Callable;

@CommandLine.Command(name="add", description = "add an event to the calendar")
public class AddSubcommand implements Callable<Integer> {

    @Override
    public Integer call() throws Exception {

        GCalApp.addEvent();

        return 0;
    }
}
