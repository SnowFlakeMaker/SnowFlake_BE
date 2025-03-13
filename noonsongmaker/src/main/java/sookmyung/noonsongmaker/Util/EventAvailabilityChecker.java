package sookmyung.noonsongmaker.Util;

import sookmyung.noonsongmaker.Entity.Chapter;

import java.util.*;

public class EventAvailabilityChecker {
    private static final Map<String, Set<Chapter>> EVENT_ELIGIBLE_CHAPTERS = Map.of(
            "개강총회", EnumSet.of(Chapter.SEM_S_1, Chapter.SEM_S_2, Chapter.SEM_S_3), // 개강총회
            "MT", EnumSet.of(Chapter.SEM_W_1, Chapter.SEM_W_2, Chapter.SEM_W_3), // MT
            "축제", EnumSet.of(Chapter.SEM_W_1, Chapter.SEM_W_2, Chapter.SEM_W_3), // 축제
            "동아리 지원", EnumSet.of(Chapter.SEM_S_1, Chapter.SEM_W_1, Chapter.SEM_S_2, Chapter.SEM_W_2, Chapter.SEM_S_3, Chapter.SEM_W_3, Chapter.SEM_S_4, Chapter.SEM_W_4), // 동아리 지원 (모든 학기)
            "전공학회 지원", EnumSet.of(Chapter.SEM_S_2, Chapter.SEM_S_3),  // 전공 학회 지원
            "대외활동 지원", EnumSet.of(Chapter.SEM_W_2, Chapter.SEM_W_3)   // 대외활동 지원
    );

    public static boolean isEventAvailable(String eventName, Chapter currentChapter) {
        return EVENT_ELIGIBLE_CHAPTERS.getOrDefault(eventName, Set.of()).contains(currentChapter);
    }

    // 특정 학기에 가능한 모든 이벤트 목록 반환
    public static List<String> getAvailableEventsForChapter(Chapter currentChapter, boolean isClubMember, boolean hasScholarship, boolean eligibleForMeritScholarship) {
        List<String> availableEvents = new ArrayList<>();

        for (Map.Entry<String, Set<Chapter>> entry : EVENT_ELIGIBLE_CHAPTERS.entrySet()) {
            if (entry.getValue().contains(currentChapter)) {
                // 동아리 가입된 경우 "동아리 지원" 제거
                if (entry.getKey().equals("동아리 지원") && isClubMember) {
                    continue;
                }
                availableEvents.add(entry.getKey());
            }
        }

        // 국가장학금 신청 가능 여부 추가 (학기 초반에 신청 가능)
        if (currentChapter.name().startsWith("SEM_") && !hasScholarship) {
            availableEvents.add("국가장학금 신청");
        }

        // 성적 장학금 지급 가능 여부 추가
        if (currentChapter.name().startsWith("SEM_") && eligibleForMeritScholarship) {
            availableEvents.add("성적 장학금 지급");
        }

        // 등록금 납부 이벤트 추가 (학기 초반)
        if (currentChapter.name().startsWith("SEM_")) {
            availableEvents.add("등록금 납부");
        }

        return availableEvents;
    }
}