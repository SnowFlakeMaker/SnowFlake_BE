package sookmyung.noonsongmaker.Entity;

import com.fasterxml.jackson.annotation.JsonValue;

public enum MajorType {
    DOUBLE_MAJOR, SUB_MAJOR, ADVANCED_MAJOR, UNKNOWN;

    @JsonValue
    public String toJson() {
        return this.name();
}

