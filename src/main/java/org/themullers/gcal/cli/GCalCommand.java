package org.themullers.gcal.cli;

import picocli.CommandLine;

@CommandLine.Command(name="gcal", subcommands = {UpcomingSubcommand.class, SubscribeSubcommand.class, CalendarsSubcommand.class})
public class GCalCommand {

    @CommandLine.Option(names = {"-v", "--verbose"})
    boolean verbose = false;

    /**
     * Run the gcal program with the provided arguments
     * @param args  parameters and options that determine the behavior of gcal
     * @return  a status code, 0 for success
     */
    public static int execute(String[] args) {

        GCalCommand gcalCommand = null;

        try {
            gcalCommand = new GCalCommand();
            return new CommandLine(gcalCommand).execute(args);
        }
        catch (Exception x) {
            if (gcalCommand == null || gcalCommand.verbose) {
                throw x;
            }
            else {
                System.err.println(x.getMessage());
                return 1;
            }
        }
    }

    public boolean isVerbose() {
        return verbose;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }
}
