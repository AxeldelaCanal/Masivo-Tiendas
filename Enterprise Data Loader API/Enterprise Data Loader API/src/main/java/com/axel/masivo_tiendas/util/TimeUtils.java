package com.axel.masivo_tiendas.util;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

public final class TimeUtils {

    private TimeUtils() {}

    // Try parse common time formats like HH:mm, HH:mm:ss
    public static LocalTime parseTime(String s) {
        if (s == null) return null;
        String in = s.trim();
        if (in.isEmpty()) return null;

        // Some values may come as decimals (Excel numeric) represented as strings
        try {
            return LocalTime.parse(in);
        } catch (DateTimeParseException e) {
            // try HH:mm
            try {
                DateTimeFormatter f = DateTimeFormatter.ofPattern("H:mm", Locale.ROOT);
                return LocalTime.parse(in, f);
            } catch (DateTimeParseException ex) {
                // try H:mm:ss
                try {
                    DateTimeFormatter f2 = DateTimeFormatter.ofPattern("H:mm:ss", Locale.ROOT);
                    return LocalTime.parse(in, f2);
                } catch (DateTimeParseException ex2) {
                    return null;
                }
            }
        }
    }

    // Convert an Apache POI Cell to LocalTime, handling numeric (date/time) and text values
    public static LocalTime fromCell(Cell cell) {
        if (cell == null) return null;

        if (cell.getCellType() == CellType.NUMERIC) {
            if (DateUtil.isCellDateFormatted(cell)) {
                return cell.getLocalDateTimeCellValue().toLocalTime();
            } else {
                double v = cell.getNumericCellValue();
                if (v < 0 || v >= 1) {
                    // Not a time fraction; can't convert safely
                    return null;
                }
                long seconds = Math.round(v * 24 * 3600);
                return LocalTime.ofSecondOfDay(seconds);
            }
        }

        String txt = cell.toString();
        return parseTime(txt);
    }
}
