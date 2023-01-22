package org.schonfeld;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import static java.time.format.DateTimeFormatter.ofPattern;

public class Application {
    public static void main(String[] args) {
        BetaCalculationEngine engine = new BetaCalculationEngine(args[0]);

      // LocalDate dd = LocalDate.parse(dateS, ofPattern("M/dd/yyyy"));
    }
}
