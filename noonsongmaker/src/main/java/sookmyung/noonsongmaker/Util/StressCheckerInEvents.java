package sookmyung.noonsongmaker.Util;

import sookmyung.noonsongmaker.Exception.StressOverflowException;

public class StressCheckerInEvents {
    public static void checkStressLimit(int currentStress, int addedStress) {
        if (currentStress + addedStress >= 100) {
            throw new StressOverflowException();
        }
    }
}
