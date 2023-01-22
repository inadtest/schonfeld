package org.schonfeld;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;

public class Utils {
    public static long calculateWeekDays(final LocalDate start, final LocalDate end) {
        final DayOfWeek sWeek = start.getDayOfWeek();
        final DayOfWeek eWeek = end.getDayOfWeek();

        final long days = ChronoUnit.DAYS.between(start, end) + 1;
        final long daysWithoutWeekends = days - 2 * ((days + sWeek.getValue())/7);

        //adjust for starting and ending on a Sunday:
        return daysWithoutWeekends + (sWeek == DayOfWeek.SUNDAY ? 1 : 0) + (eWeek == DayOfWeek.SUNDAY ? 1 : 0);
    }

    public static LocalDate getPreviousWorkingDay(LocalDate date) {
        DayOfWeek dayOfWeek = DayOfWeek.of(date.get(ChronoField.DAY_OF_WEEK));
        switch (dayOfWeek) {
            case MONDAY:
                return date.minus(3, ChronoUnit.DAYS);
            // case SUNDAY:
            //     return date.minus(2, ChronoUnit.DAYS);
            default:
                return date.minus(1, ChronoUnit.DAYS);

        }
    }

    public static LocalDate getNextWorkingDay(LocalDate date) {
        DayOfWeek dayOfWeek = DayOfWeek.of(date.get(ChronoField.DAY_OF_WEEK));
        switch (dayOfWeek) {
            case FRIDAY:
                return date.plus(3, ChronoUnit.DAYS);
            default:
                return date.plus(1, ChronoUnit.DAYS);
        }
    }

    public static LocalDate subtractDaysSkippingWeekends(LocalDate date, long days) {
        LocalDate result = date;
        long subtractedDays = 0;
        while (subtractedDays < days-1) {
            result = result.minusDays(1);
            if (!(result.getDayOfWeek() == DayOfWeek.SATURDAY || result.getDayOfWeek() == DayOfWeek.SUNDAY)) {
                ++subtractedDays;
            }
        }
        return result;
    }
}
