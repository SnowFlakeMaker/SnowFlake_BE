package sookmyung.noonsongmaker.Service.intro;

import sookmyung.noonsongmaker.Entity.MBTI;
import sookmyung.noonsongmaker.Entity.StatusInfo;
import sookmyung.noonsongmaker.Entity.User;
import org.springframework.stereotype.Service;

@Service
public class MBTIStatusService {

    public StatusInfo createInitialStatus(User user, MBTI mbti) {

        // 기본값은 @Builder.Default가 자동 설정
        StatusInfo.StatusInfoBuilder builder = StatusInfo.builder()
                .user(user);

        // UNKNOWN이 아닌 경우 필요한 속성 변경
        if (mbti != MBTI.UNKNOWN) {
            String mbtiStr = mbti.name();

            // I vs E
            if (mbtiStr.charAt(0) == 'I') {
                builder.strength(20).social(20);
            } else {
                builder.strength(30).social(30);
            }

            // S vs N
            if (mbtiStr.charAt(1) == 'S') {
                builder.stress(20);
            } else {
                builder.stress(30);
            }

            // T vs F
            if (mbtiStr.charAt(2) == 'T') {
                builder.leadership(30);
            } else {
                builder.leadership(20);
            }

            // J vs P
            if (mbtiStr.charAt(3) == 'J') {
                builder.grit(30);
            } else {
                builder.grit(20);
            }
        }

        return builder.build();
    }
}
