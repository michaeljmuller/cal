package org.themullers.gcal.cli;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import picocli.CommandLine;

@CommandLine.Command(name="gcal",
        subcommands = {
            UpcomingSubcommand.class,
            SubscribeSubcommand.class,
            CalendarsSubcommand.class,
            EmailSubcommand.class,
            AddSubcommand.class,
        })
public class GCalCommand implements CommandLine.IExecutionExceptionHandler {

    @CommandLine.Option(names = {"-v", "--verbose"})
    boolean verbose = false;

    /**
     * Run the gcal program with the provided arguments
     * @param args  parameters and options that determine the behavior of gcal
     * @return  a status code, 0 for success
     */
    public static int execute(String[] args) {
        GCalCommand gcalCommand = new GCalCommand();
        return new CommandLine(gcalCommand).setExecutionExceptionHandler(gcalCommand).execute(args);
    }

    public boolean isVerbose() {
        return verbose;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    @Override
    public int handleExecutionException(Exception e, CommandLine commandLine, CommandLine.ParseResult parseResult) throws Exception {
        int resultCode = 1;

        // if the user has requested verbose output, dump the exception and stack trace syserr
        if (isVerbose()) {
            e.printStackTrace(System.err);
        }
        else {

            // if the exception was caused by a GoogleJsonResponseException, get the status message
            // because the actual message is a verbose multi-line response
            var gjrx = getGoogleJsonResponseExceptionCause(e);
            if (gjrx != null) {
                System.err.println(gjrx.getStatusMessage());
                resultCode = gjrx.getStatusCode();
            }

            // otherwise just echo the message
            else {
                System.err.println(e.getMessage());
            }
        }
        return resultCode;
    }

    protected GoogleJsonResponseException getGoogleJsonResponseExceptionCause(Throwable e) {
        if (e == null) {
            return null;
        }
        if (e instanceof GoogleJsonResponseException gjrx) {
            return gjrx;
        }
        return getGoogleJsonResponseExceptionCause(e.getCause());
    }
}
