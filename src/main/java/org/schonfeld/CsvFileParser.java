package org.schonfeld;

import io.vavr.control.Try;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.File;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.commons.csv.CSVFormat.EXCEL;

public class CsvFileParser {
    private final File file;

    CsvFileParser(String file) {
        this.file = new File(file);
    }

    List<CSVRecord> parse() {
        return Try.of(() ->
                        CSVParser.parse(file, UTF_8, EXCEL.withFirstRecordAsHeader()).getRecords()).
                getOrElseThrow(() -> new RuntimeException("File not found"));
    }
}
