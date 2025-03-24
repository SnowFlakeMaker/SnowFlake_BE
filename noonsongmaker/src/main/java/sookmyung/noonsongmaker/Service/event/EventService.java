package sookmyung.noonsongmaker.Service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sookmyung.noonsongmaker.Entity.Event;
import sookmyung.noonsongmaker.Entity.EventChapters;
import sookmyung.noonsongmaker.Entity.User;
import sookmyung.noonsongmaker.Repository.EventChaptersRepository;
import sookmyung.noonsongmaker.Repository.EventRepository;
import sookmyung.noonsongmaker.Repository.StatusInfoRepository;
import sookmyung.noonsongmaker.Repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventChaptersRepository eventChaptersRepository;


    public List<String> getRegularEvents(User user) {
        return eventChaptersRepository.findByUserAndIsActivatedTrue(user).stream()
                .map(EventChapters::getEvent)
                .filter(Event::isRegular)
                .filter(event -> event.getActivatedChapters().contains(user.getCurrentChapter()))
                .filter(event -> !"등록금 대리납부".equals(event.getName()))
                .map(Event::getName)
                .collect(Collectors.toList());
    }

    public List<String> getOneTimeEvents(User user) {
        return eventChaptersRepository.findByUserAndIsActivatedTrue(user).stream()
                .map(EventChapters::getEvent)
                .filter(event -> !event.isRegular())
                .filter(event -> event.getActivatedChapters().contains(user.getCurrentChapter()))
                .map(Event::getName)
                .collect(Collectors.toList());
    }

}
