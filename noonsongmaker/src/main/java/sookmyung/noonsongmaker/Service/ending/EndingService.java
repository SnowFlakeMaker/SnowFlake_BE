package sookmyung.noonsongmaker.Service.ending;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sookmyung.noonsongmaker.Dto.ending.EndingResponseDto;
import sookmyung.noonsongmaker.Entity.StatusInfo;
import sookmyung.noonsongmaker.Entity.User;
import sookmyung.noonsongmaker.Entity.UserProfile;
import sookmyung.noonsongmaker.Repository.StatusInfoRepository;
import sookmyung.noonsongmaker.Repository.UserProfileRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class EndingService {
    private final StatusInfoRepository statusInfoRepository;
    private final UserProfileRepository userProfileRepository;
    private final int GOOD = 150;
    private final int SPECIAL = 50;
    private final int NORMAL = 50;
    private final int EVERY_NORMAL = 20;
    private final int STAT_NORMAL = 10;


    public EndingResponseDto getEnding(User user) {
        StatusInfo statusInfo = statusInfoRepository.findByUser(user)
                .orElseThrow(() -> new NoSuchElementException("No status info found for user: " + user));
        UserProfile userProfile = userProfileRepository.findByUser(user)
                .orElseThrow(() -> new NoSuchElementException("No profile found for user: " + user));

        if (isGoodEnding(statusInfo)) { return EndingResponseDto.builder().endingType("굿 엔딩").build();}

        String specialEnding = getSpecialEnding(statusInfo);
        if (specialEnding != null) { return EndingResponseDto.builder()
                .endingType("스페셜 엔딩")
                .endingText(specialEnding)
                .build(); }

        // 모든 스탯이 50 이하
        if (isNormalEnding(statusInfo)) {
            String normalEnding = getNormalEnding(statusInfo);
            // 모든 스탯이 20 이하
            if (normalEnding.equals("꿈 엔딩")) { return EndingResponseDto.builder()
                    .endingType("노말 엔딩")
                    .endingText(normalEnding)
                    .dream(userProfile.getDream())
                    .build(); }
            // 그 외
            else { return EndingResponseDto.builder()
                        .endingType("노말 엔딩")
                        .endingText(normalEnding)
                        .build();}
        }

        // 모든 스탯이 50 초과
        return EndingResponseDto.builder()
                .endingType("스탯 엔딩")
                .endingText(getStatEnding(statusInfo))
                .build();

    }

    private boolean isGoodEnding(StatusInfo statusInfo) {
        return statusInfo.getIntelligence() == GOOD &&
               statusInfo.getForeignLang() == GOOD &&
               statusInfo.getGrit() == GOOD &&
               statusInfo.getStrength() == GOOD &&
               statusInfo.getSocial() == GOOD &&
               statusInfo.getStress() == GOOD &&
               statusInfo.getLeadership() == GOOD &&
               statusInfo.getGeneralAssess() == GOOD;
    }


    private String getSpecialEnding(StatusInfo statusInfo) {
        Map<String, Integer> assessMap = new HashMap<>();
        if (statusInfo.getHobbyAssess() >= SPECIAL) assessMap.put("취미 엔딩", statusInfo.getHobbyAssess());
        if (statusInfo.getWorkAssess() >= SPECIAL) assessMap.put("알바 엔딩", statusInfo.getWorkAssess());
        if (statusInfo.getServiceAssess() >= SPECIAL) assessMap.put("봉사 엔딩", statusInfo.getServiceAssess());
        if (statusInfo.getGlobalAssess() >= SPECIAL) assessMap.put("해외 엔딩", statusInfo.getGlobalAssess());

        return assessMap.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    private String getStatEnding(StatusInfo statusInfo) {
        Map<String, Integer> statMap = new HashMap<>();
        statMap.put("근성", statusInfo.getGrit());
        statMap.put("리더십", statusInfo.getLeadership());
        statMap.put("지력", statusInfo.getIntelligence());
        statMap.put("체력", statusInfo.getStrength());
        statMap.put("사회성", statusInfo.getSocial());
        statMap.put("외국어", statusInfo.getForeignLang());

        String highestStat = statMap.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElseThrow(() -> new IllegalStateException("스탯 비교에 실패했습니다."));

        switch (highestStat) {
            case "체력": {
                if (statusInfo.getGrit() > NORMAL) { return "군인"; }
                else return "짐 대표";
            }
            case "지력": {
                if (statusInfo.getGrit() > NORMAL) { return "연구원"; }
                else return "대학원생";
            }
            case "근성": {
                return "전문직";
            }
            case "사회성": {
                if (statusInfo.getGrit() > NORMAL) { return "창업"; }
                else return "인플루언서";
            }
            case "리더십": {
                if (statusInfo.getGrit() > NORMAL) { return "대통령"; }
                else return "청년 국회의원";
            }
            case "외국어": {
                if (statusInfo.getGrit() > NORMAL) { return "통역가"; }
                else return "번역가";
            }
            default: return  "스탯 비교 실패";
        }
    }

    private Boolean isNormalEnding(StatusInfo statusInfo) {
        return statusInfo.getIntelligence() <= NORMAL &&
                statusInfo.getForeignLang() <= NORMAL &&
                statusInfo.getGrit() <= NORMAL &&
                statusInfo.getStrength() <= NORMAL &&
                statusInfo.getSocial() <= NORMAL &&
                statusInfo.getStress() <= NORMAL &&
                statusInfo.getLeadership() <= NORMAL &&
                statusInfo.getGeneralAssess() <= NORMAL;
    }

    private String getNormalEnding(StatusInfo statusInfo) {
        if (isEveryStatsUnderTwenty(statusInfo)) {
            return "꿈 엔딩";
        }
        Map<String, Integer> statMap = new HashMap<>();
        statMap.put("근성", statusInfo.getGrit());
        statMap.put("리더십", statusInfo.getLeadership());
        statMap.put("지력", statusInfo.getIntelligence());
        statMap.put("체력", statusInfo.getStrength());
        statMap.put("사회성", statusInfo.getSocial());

        String lowestStat = statMap.entrySet().stream()
                .min(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElseThrow(() -> new IllegalStateException("스탯 비교에 실패했습니다."));
        return switch (lowestStat) {
            case "체력" -> "체력 엔딩";
            case "지력" -> "지력 엔딩";
            case "근성" -> "근성 엔딩";
            case "사회성" -> "사회성 엔딩";
            case "리더십" -> "리더십 엔딩";
            default -> "스탯 비교 실패";
        };

    }

    private boolean isEveryStatsUnderTwenty(StatusInfo statusInfo) {
        return statusInfo.getIntelligence() <= EVERY_NORMAL &&
                statusInfo.getForeignLang() <= EVERY_NORMAL &&
                statusInfo.getGrit() <= EVERY_NORMAL &&
                statusInfo.getStrength() <= EVERY_NORMAL &&
                statusInfo.getSocial() <= EVERY_NORMAL &&
                statusInfo.getStress() <= EVERY_NORMAL &&
                statusInfo.getLeadership() <= EVERY_NORMAL &&
                statusInfo.getGeneralAssess() <= EVERY_NORMAL;
    }
}
