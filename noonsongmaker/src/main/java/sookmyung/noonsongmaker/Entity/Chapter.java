package sookmyung.noonsongmaker.Entity;

public enum Chapter {
    SEM_S_1("1학년 1학기", 1), VAC_S_1("1학년 여름방학", 1), SEM_W_1("1학년 2학기", 2), VAC_W_1("1학년 겨울방학", 2),
    SEM_S_2("2학년 1학기", 3), VAC_S_2("2학년 여름방학", 3), SEM_W_2("2학년 2학기", 4), VAC_W_2("2학년 겨울방학", 4),
    SEM_S_3("3학년 1학기", 5), VAC_S_3("3학년 여름방학", 5), SEM_W_3("3학년 2학기", 6), VAC_W_3("3학년 겨울방학", 6),
    SEM_S_4("4학년 1학기", 7), VAC_S_4("4학년 여름방학", 7), SEM_W_4("4학년 2학기", 8), VAC_W_4("4학년 겨울방학", 8);

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
}