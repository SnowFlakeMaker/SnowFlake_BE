package sookmyung.noonsongmaker.Service.course;

import sookmyung.noonsongmaker.Entity.Chapter;

import java.util.Random;

public class TimetableUpdatePolicy {
    private final Chapter chapter;
    private final Random random = new Random();

    public TimetableUpdatePolicy(Chapter chapter) {
        this.chapter = chapter;
    }


    public boolean isMajorUpdatePossible() {
        return random.nextDouble() < getMajorSuccessRate();
    }

    public boolean isDmUpdatePossible() { return random.nextDouble() < getDmSuccessRate(); }

    public boolean isLibUpdatePossible() {
        return random.nextDouble() < getLibSuccessRate();
    }


    private double getMajorSuccessRate() {
        return switch (chapter) {
            case SEM_S_1, SEM_W_1, SEM_S_2, SEM_W_2 -> 0.8;
            case SEM_S_3, SEM_W_3 -> 0.9;
            case SEM_S_4, SEM_W_4 -> 1.0;
            default -> throw new IllegalStateException("Unexpected value: " + chapter);
        };
    }

    private double getDmSuccessRate() {
        return switch (chapter) {
            case SEM_S_1, SEM_W_1 -> 0.4;
            case SEM_S_2, SEM_W_2 -> 0.5;
            case SEM_S_3, SEM_W_3 -> 0.6;
            case SEM_S_4, SEM_W_4 -> 0.7;
            default -> throw new IllegalStateException("Unexpected value: " + chapter);
        };
    }

    private double getLibSuccessRate() {
        return switch (chapter) {
            case SEM_S_1, SEM_W_1 -> 0.9;
            case SEM_S_2, SEM_W_2 -> 0.1;
            case SEM_S_3, SEM_W_3 -> 0.2;
            case SEM_S_4, SEM_W_4 -> 1.0;
            default -> throw new IllegalStateException("Unexpected value: " + chapter);
        };
    }
}
