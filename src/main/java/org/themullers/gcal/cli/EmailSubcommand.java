package org.themullers.gcal.cli;

import org.themullers.gcal.GCalApp;
import picocli.CommandLine;

import java.util.concurrent.Callable;

@CommandLine.Command(name="email", description = "display the service account email from the credentials")
public class EmailSubcommand implements Callable<Integer> {

    @Override
    public Integer call() throws Exception {
        GCalApp.serviceAccountEmail();
        return 0;
    }
}
