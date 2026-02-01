package com.axel.masivo_tiendas.util;

import org.junit.jupiter.api.Test;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class TimeUtilsTest {

    @Test
    void parseCommonFormats() {
        assertEquals(LocalTime.of(0,0), TimeUtils.parseTime("00:00"));
        assertEquals(LocalTime.of(0,0,0), TimeUtils.parseTime("00:00:00"));
        assertEquals(LocalTime.of(9,5), TimeUtils.parseTime("9:05"));
        assertEquals(LocalTime.of(23,59,59), TimeUtils.parseTime("23:59:59"));
        assertNull(TimeUtils.parseTime(""));
        assertNull(TimeUtils.parseTime(null));
    }
}
