package sookmyung.noonsongmaker.Dto.event;

import lombok.Getter;
import sookmyung.noonsongmaker.Entity.StatusInfo;

@Getter
public class CoinResponseDto {
    private final int coin;

    public CoinResponseDto(StatusInfo statusInfo) {
        this.coin = statusInfo.getCoin();
    }
}