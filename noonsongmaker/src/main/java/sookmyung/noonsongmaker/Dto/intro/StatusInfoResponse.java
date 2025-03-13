package sookmyung.noonsongmaker.Dto.intro;

import lombok.Getter;
import sookmyung.noonsongmaker.Entity.StatusInfo;

@Getter
public class StatusInfoResponse {
    private int intelligence;
    private int foreignLang;
    private int grit;
    private int strength;
    private int social;
    private int stress;
    private int leadership;
    private int generalAssess;
    private int hobbyAssess;
    private int workAssess;
    private int serviceAssess;
    private int globalAssess;
    private int coin;

    public StatusInfoResponse(StatusInfo statusInfo) {
        this.intelligence = statusInfo.getIntelligence();
        this.foreignLang = statusInfo.getForeignLang();
        this.grit = statusInfo.getGrit();
        this.strength = statusInfo.getStrength();
        this.social = statusInfo.getSocial();
        this.stress = statusInfo.getStress();
        this.leadership = statusInfo.getLeadership();
        this.generalAssess = statusInfo.getGeneralAssess();
        this.hobbyAssess = statusInfo.getHobbyAssess();
        this.workAssess = statusInfo.getWorkAssess();
        this.serviceAssess = statusInfo.getServiceAssess();
        this.globalAssess = statusInfo.getGlobalAssess();
        this.coin = statusInfo.getCoin();
    }
}
