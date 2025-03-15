package sookmyung.noonsongmaker.Dto.event;

import lombok.Getter;
import sookmyung.noonsongmaker.Entity.StatusInfo;

@Getter
public class ClubResponseDto {
    private boolean isClubMember; // 동아리 가입 여부

    public ClubResponseDto(StatusInfo statusInfo) {
        this.isClubMember = statusInfo.isClubMember();
    }
}