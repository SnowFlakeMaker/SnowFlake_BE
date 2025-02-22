package sookmyung.noonsongmaker.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sookmyung.noonsongmaker.Dto.course.CreditResponseDto;
import sookmyung.noonsongmaker.Entity.Course;
import sookmyung.noonsongmaker.Entity.User;
import sookmyung.noonsongmaker.Entity.UserProfile;
import sookmyung.noonsongmaker.Repository.CourseRepository;
import sookmyung.noonsongmaker.Repository.UserProfileRepository;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class CourseService {
    private final CourseRepository courseRepository;
    private final UserProfileRepository userProfileRepository;

    public CreditResponseDto getCurrentCreditStatus(User user) {
        Course courseResult = courseRepository.findByUser(user)
                .orElseThrow(() -> new NoSuchElementException("수강 정보 소유자 없음"));
        UserProfile userProfileResult = userProfileRepository.findByUser(user)
                .orElseThrow(() -> new NoSuchElementException("사용자 없음"));


        return CreditResponseDto.builder()
                .semester(user.getCurrentChapter())
                .major(userProfileResult.getMajor())
                .majorType(userProfileResult.getMajorType())
                .CurrentCoreCredits(courseResult.getCoreCredits())
                .CurrentElectivesCredits(courseResult.getElectiveCredits())
                .build();
    }
}
