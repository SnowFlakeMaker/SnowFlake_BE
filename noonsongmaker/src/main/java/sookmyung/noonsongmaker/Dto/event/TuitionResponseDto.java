package sookmyung.noonsongmaker.Dto.event;

import lombok.Getter;
import sookmyung.noonsongmaker.Entity.StatusInfo;

@Getter
public class TuitionResponseDto {
    private final int coin;
    private final boolean isSuccess;

    public TuitionResponseDto(StatusInfo statusInfo, boolean isSuccess) {
        this.coin = statusInfo.getCoin();
        this.isSuccess = isSuccess;
    }
}