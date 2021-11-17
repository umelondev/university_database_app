package com.foxminded.javaee.university.model;

import java.util.Locale;

public enum LectureTime {
    FIRST("8:00-9:20"),
    SECOND("9:35-10:55"),
    THIRD("11:25-12:45"),
    FOURTH("12:55-14:15");

    private final String time;

    LectureTime(String time) {
        this.time = time;
    }

    public String getTime() {
        return time;
    }

    public static LectureTime of(String value) {
        return LectureTime.valueOf(value.toUpperCase(Locale.ROOT));
    }
}
