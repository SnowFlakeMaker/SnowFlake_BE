package sookmyung.noonsongmaker.Dto.main;

import lombok.Getter;
import sookmyung.noonsongmaker.Entity.Chapter;

@Getter
public class ChapterResponseDto {
    private final String chapter;

    public ChapterResponseDto(Chapter chapter) {
        this.chapter = chapter.getDescription(); // "1학년 1학기" 등의 한글 변환된 값
    }
}