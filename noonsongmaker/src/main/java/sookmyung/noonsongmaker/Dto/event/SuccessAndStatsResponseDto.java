package sookmyung.noonsongmaker.Dto.event;


import lombok.Getter;

@Getter
public class SuccessAndStatsResponseDto {

    private final boolean success;
    private StatsResponseDto statusInfo;


    public SuccessAndStatsResponseDto(boolean success, StatsResponseDto statsResponseDto) {
        this.success = success;
        this.statusInfo = statsResponseDto;
    }
}