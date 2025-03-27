package sookmyung.noonsongmaker.Dto.event;

import lombok.Getter;
import sookmyung.noonsongmaker.Entity.StatusInfo;

@Getter
public class CoinAndStressResponseDto {
    private final int coin;
    private final int stress;

    public CoinAndStressResponseDto(StatusInfo statusInfo) {
        this.coin = statusInfo.getCoin();
        this.stress = statusInfo.getStress();
    }
}