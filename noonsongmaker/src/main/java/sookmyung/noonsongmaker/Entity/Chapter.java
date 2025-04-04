package sookmyung.noonsongmaker.Entity;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Chapter {
    NEWBIE("예비 눈송이", 0),
    SEM_S_1("1학년 1학기", 1), VAC_S_1("1학년 여름방학", 1), SEM_W_1("1학년 2학기", 2), VAC_W_1("1학년 겨울방학", 2),
    SEM_S_2("2학년 1학기", 3), VAC_S_2("2학년 여름방학", 3), SEM_W_2("2학년 2학기", 4), VAC_W_2("2학년 겨울방학", 4),
    SEM_S_3("3학년 1학기", 5), VAC_S_3("3학년 여름방학", 5), SEM_W_3("3학년 2학기", 6), VAC_W_3("3학년 겨울방학", 6),
    SEM_S_4("4학년 1학기", 7), VAC_S_4("4학년 여름방학", 7), SEM_W_4("4학년 2학기", 8), VAC_W_4("4학년 겨울방학", 8),
    ENDING("엔딩",9);

    private final String description;
    private final int semester;

    Chapter(String description, int semester) {
        this.description = description;
        this.semester = semester;
    }

    public String getDescription() {
        return description;
    }

    public int getSemester() {
        return semester;
    }

    @JsonValue
    public String toJson() {
        return this.name();

    }

    public static Chapter getNextChapter(Chapter currentChapter) {
        Chapter[] chapters = Chapter.values();
        for (int i = 0; i < chapters.length - 1; i++) {
            if (chapters[i] == currentChapter) {
                return chapters[i + 1];
            }
        }
        return null;  // 마지막 챕터(VAC_W_4) 이후는 없음
    }
}