package com.Train.TrainService.Enum;

public enum ClassType {
    AC_1A("1A - AC First Class"),
    AC_2A("2A - AC 2-Tier"),
    AC_3A("3A - AC 3-Tier"),
    SLEEPER("SL - Sleeper"),
    SECOND_SITTING("2S - Second Sitting"),
    AC_CHAIR_CAR("CC - AC Chair Car"),
    FIRST_CLASS("FC - First Class"),
    THIRD_AC_ECONOMY("3E - AC 3-Tier Economy");

    private final String description;

    ClassType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
