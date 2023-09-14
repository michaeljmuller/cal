package org.themullers;

import org.junit.Test;
import org.themullers.gcal.cli.GCalCommand;

public class CliTest {

    protected int run(String... args) {
        return GCalCommand.execute(args);
    }

    @Test
    public void testNoArgs() throws Exception {
        assert run() > 0;
    }

    @Test
    public void testUpcomingNoCalID() throws Exception {
        assert run("upcoming") > 0;
    }

    @Test
    public void testUpcoming() throws Exception {
        assert run("upcoming", "mike@themullers.org") == 0;
    }

    @Test
    public void testUpcomingNumber() throws Exception {
        assert run("upcoming", "-n", "15", "mike@themullers.org") == 0;
    }

    @Test
    public void testSubscribeNoCalID() throws Exception {
        run("subscribe");
    }

    @Test
    public void testSubscribe() throws Exception {
        run("subscribe", "mike@themullers.org");
    }

    @Test
    public void testCalendars() throws Exception {
        run("calendars");
    }
}
