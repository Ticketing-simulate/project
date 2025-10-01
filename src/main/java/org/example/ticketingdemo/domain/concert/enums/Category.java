package org.example.ticketingdemo.domain.concert.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum Category {
    POP, ROCK, HIP_HOP, JAZZ, CLASSICAL, BALLAD, INDIE, ELECTRONIC, TROT, MUSICAL, KPop,
    K_Pop("K-Pop"), // DB 값 지정

    PLAY, COMEDY, TRAGEDY, DRAMA, MONOLOGUE, PERFORMANCE;

    private final String dbValue;

    Category() {
        this.dbValue = this.name();
    }

    Category(String dbValue) {
        this.dbValue = dbValue;
    }

    public String getDbValue() {
        return dbValue;
    }

    public static Category fromDbValue(String dbValue) {
        for (Category c : values()) {
            if (c.dbValue.equals(dbValue)) return c;
        }
        throw new IllegalArgumentException("Unknown Category: " + dbValue);
    }
}

