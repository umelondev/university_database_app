package com.foxminded.javaee.university.model;

import java.util.Locale;

public enum Degree {
    BACHELOR("Bachelor’s Degree"),
    MASTER("Master’s Degree"),
    ASSOCIATE("Associate Degree"),
    FIRST_PROFESSIONAL("First-professional Degree"),
    DOCTORAL("Doctoral Degree");

    private final String description;

    Degree(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static Degree of(String value) {
        return Degree.valueOf(value.toUpperCase(Locale.ROOT));
    }
}
