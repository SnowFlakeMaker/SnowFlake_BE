package sookmyung.noonsongmaker.Dto.event;

import lombok.Getter;
import sookmyung.noonsongmaker.Entity.StatusInfo;

@Getter
public class TuitionResponseDto {
    private final int coin;
    private final boolean success;

    public TuitionResponseDto(StatusInfo statusInfo, boolean success) {
        this.coin = statusInfo.getCoin();
        this.success = success;
    }
}

