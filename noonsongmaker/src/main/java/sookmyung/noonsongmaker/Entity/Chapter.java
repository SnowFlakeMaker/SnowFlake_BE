package sookmyung.noonsongmaker.Entity;

public enum Chapter {
    SEM_S_1("1학년 1학기"), VAC_S_1("1학년 여름방학"), SEM_W_1("1학년 2학기"), VAC_W_1("1학년 겨울방학"),
    SEM_S_2("2학년 1학기"), VAC_S_2("2학년 여름방학"), SEM_W_2("2학년 2학기"), VAC_W_2("2학년 겨울방학"),
    SEM_S_3("3학년 1학기"), VAC_S_3("3학년 여름방학"), SEM_W_3("3학년 2학기"), VAC_W_3("3학년 겨울방학"),
    SEM_S_4("4학년 1학기"), VAC_S_4("4학년 여름방학"), SEM_W_4("4학년 2학기"), VAC_W_4("4학년 겨울방학");

    private final String description;

    Chapter(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}