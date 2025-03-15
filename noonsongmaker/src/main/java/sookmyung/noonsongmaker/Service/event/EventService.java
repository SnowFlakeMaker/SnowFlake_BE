package sookmyung.noonsongmaker.Service.event;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sookmyung.noonsongmaker.Entity.*;
import sookmyung.noonsongmaker.Repository.EventChaptersRepository;
import sookmyung.noonsongmaker.Repository.EventRepository;
import sookmyung.noonsongmaker.Repository.StatusInfoRepository;
import sookmyung.noonsongmaker.Repository.UserRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventService {

    private final StatusInfoRepository statusInfoRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final EventChaptersRepository eventChaptersRepository;

    // 현재 학기에서 가능한 모든 이벤트 조회
    public List<String> getAvailableEvents(Long userId, Chapter currentChapter) {
        User user = getUser(userId);
        StatusInfo statusInfo = getUserStatus(user);

        List<EventChapters> events = eventChaptersRepository.findByActivatedChapter(currentChapter);

        return events.stream()
                .map(eventChapter -> eventChapter.getEvent().getName())
                .collect(Collectors.toList());
    }

    // 새로운 이벤트를 특정 챕터에 추가
    @Transactional
    public void addEventToChapter(String eventName, Chapter chapter, boolean isProbabilistic, Float probability) {
        // 이벤트가 이미 존재하는지 확인
        Event event = eventRepository.findByName(eventName)
                .orElseGet(() -> {
                    // 이벤트가 없으면 새로 생성 후 저장
                    Event newEvent = Event.builder()
                            .name(eventName)
                            .isProbabilistic(isProbabilistic)
                            .probability(probability)
                            .build();
                    return eventRepository.save(newEvent);
                });

        // 이미 해당 챕터에 등록된 이벤트인지 확인 (중복 방지)
        boolean alreadyExists = eventChaptersRepository.existsByEventAndActivatedChapter(event, chapter);
        if (alreadyExists) {
            throw new IllegalArgumentException("이미 해당 챕터에 등록된 이벤트입니다.");
        }

        // 이벤트 챕터에 추가
        EventChapters eventChapter = EventChapters.builder()
                .event(event)
                .activatedChapter(chapter)
                .build();

        eventChaptersRepository.save(eventChapter);
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 유저입니다."));
    }

    private StatusInfo getUserStatus(User user) {
        return statusInfoRepository.findByUser(user)
                .orElseThrow(() -> new NoSuchElementException("유저 상태 정보가 존재하지 않습니다."));
    }


}
