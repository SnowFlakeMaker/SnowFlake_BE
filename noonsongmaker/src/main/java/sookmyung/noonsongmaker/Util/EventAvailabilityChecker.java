package sookmyung.noonsongmaker.Util;

import sookmyung.noonsongmaker.Entity.Chapter;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

public class EventAvailabilityChecker {
    private static final Map<String, Set<Chapter>> EVENT_ELIGIBLE_CHAPTERS = Map.of(
            "orientation", EnumSet.of(Chapter.SEM_S_1, Chapter.SEM_S_2, Chapter.SEM_S_3), // 1~3학년 1학기
            "festival", EnumSet.of(Chapter.SEM_W_1, Chapter.SEM_W_2, Chapter.SEM_W_3), // 1~3학년 2학기
            "mt", EnumSet.of(Chapter.SEM_W_1, Chapter.SEM_W_2, Chapter.SEM_W_3) // 1~3학년 2학기
    );

    public static boolean isEventAvailable(String eventName, Chapter currentChapter) {
        return EVENT_ELIGIBLE_CHAPTERS.getOrDefault(eventName, Set.of()).contains(currentChapter);
    }
}