package org.themullers;

import com.zoho.hawking.HawkingTimeParser;
import com.zoho.hawking.datetimeparser.configuration.HawkingConfiguration;
import com.zoho.hawking.language.english.model.DateRange;
import org.junit.Test;

import java.util.Date;
import java.util.logging.LogManager;

public class DateParsing {

    @Test
    public void test() {
        LogManager.getLogManager().reset();
        var parser = new HawkingTimeParser();
        var dates = parser.parse("tomorrow", new Date(), new HawkingConfiguration(), "eng");
        var start = dates.getParserOutputs().get(0).getDateRange().getStart();
        System.out.println("is all day? " + isAllDay(dates.getParserOutputs().get(0).getDateRange()));
        System.out.println(dates);
    }

    protected boolean isAllDay(DateRange range) {
        return range.getStart().getHourOfDay() == 0 && range.getEnd().getHourOfDay() == 23 &&
                range.getStart().getMinuteOfHour() == 0 && range.getEnd().getMinuteOfHour() == 59 &&
                range.getStart().getSecondOfMinute() == 0 && range.getEnd().getMinuteOfHour() == 59;
    }
}
