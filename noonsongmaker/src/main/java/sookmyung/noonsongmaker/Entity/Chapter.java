package sookmyung.noonsongmaker.Entity;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Chapter {
    SEM_S_1, VAC_S_1, SEM_W_1, VAC_W_1,
    SEM_S_2, VAC_S_2, SEM_W_2, VAC_W_2,
    SEM_S_3, VAC_S_3, SEM_W_3, VAC_W_3,
    SEM_S_4, VAC_S_4, SEM_W_4, VAC_W_4;

    @JsonValue
    public String toJson() {
        return this.name();
    }
}