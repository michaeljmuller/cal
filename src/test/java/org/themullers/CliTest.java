package org.themullers;

import org.junit.Test;
import org.themullers.gcal.cli.GCalCommand;

public class CliTest {

    protected int run(String... args) {
        return GCalCommand.execute(args);
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


}
