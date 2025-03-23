package sookmyung.noonsongmaker.Dto.event;


import lombok.Getter;

@Getter
public class ExchangeProgramResponseDto {

    private final boolean success;
    private StatsResponseDto statusInfo;


    public ExchangeProgramResponseDto(boolean success, StatsResponseDto statsResponseDto) {
        this.success = success;
        this.statusInfo = statsResponseDto;
    }
}