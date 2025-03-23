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


    public List<String> getRegularEvents(User user) {
        return eventChaptersRepository.findByUserAndIsActivatedTrue(user).stream()
                .map(EventChapters::getEvent)
                .filter(Event::isRegular)
                .map(Event::getName)
                .collect(Collectors.toList());
    }

    public List<String> getOneTimeEvents(User user) {
        return eventChaptersRepository.findByUserAndIsActivatedTrue(user).stream()
                .map(EventChapters::getEvent)
                .filter(event -> !event.isRegular())
                .map(Event::getName)
                .collect(Collectors.toList());
    }

}
