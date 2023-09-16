package org.themullers;

import org.junit.Test;
import org.themullers.gcal.cli.GCalCommand;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.function.Supplier;

public class CliTest {

    protected int run(String... args) {
        return GCalCommand.execute(args);
    }

    protected int captureOutput(StringBuffer output, StringBuffer error, Supplier<Integer> process) throws IOException {

        int retval = 0;

        try (var outputBaos = new ByteArrayOutputStream();
             var outputPs = new PrintStream(outputBaos);
             var errorBaos = new ByteArrayOutputStream();
             var errorPs = new PrintStream(errorBaos);
             )
        {
            var oldOut = System.out;
            var oldErr = System.err;

            // redirect output into the byte array
            if (output != null) {
                System.out.flush();
                System.setOut(outputPs);
            }

            // redirect error into the other byte array
            if (error != null) {
                System.err.flush();
                System.setErr(errorPs);
            }

            // execute the process
            retval = process.get();

            // un-redirect the output and write the captured byte array to the string buffer
            if (output != null) {
                System.out.flush();
                System.setOut(oldOut);
                output.append(outputBaos.toString());
            }

            // un-redirect the error and write the captured byte array to the string buffer
            if (error != null) {
                System.err.flush();
                System.setErr(oldErr);
                error.append(errorBaos.toString());
            }
        }

        return retval;
    }

    @Test
    public void testNoArgs() {
        assert run() > 0;
    }

    @Test
    public void testUpcomingNoCalID() {
        assert run("upcoming") > 0;
    }

    @Test
    public void testUpcoming() {
        assert run("upcoming", "mike@themullers.org") == 0;
    }

    @Test
    public void testUpcomingBadArg() throws IOException {
        var error = new StringBuffer();
        var status = captureOutput(null, error, () -> run("upcoming", "bogus calendar id"));
        assert status != 0;
        assert error.toString().trim().equals("Not Found");
    }

    @Test
    public void testUpcomingBadArgVerbose() {
        run("-v", "upcoming", "bogus calendar id");
    }

    @Test
    public void testUpcomingNumber() {
        assert run("upcoming", "-n", "15", "mike@themullers.org") == 0;
    }

    @Test
    public void testSubscribeNoCalID() {
        run("subscribe");
    }

    @Test
    public void testSubscribe() {
        run("subscribe", "mike@themullers.org");
    }

    @Test
    public void testCalendars() {
        run("calendars");
    }

    @Test
    public void testEmail() {
        run("email");
    }

    @Test
    public void testAddEvent() {
        run("add");
    }

}
