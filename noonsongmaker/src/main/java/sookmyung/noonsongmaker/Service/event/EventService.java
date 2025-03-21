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


    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 유저입니다."));
    }

    private StatusInfo getUserStatus(User user) {
        return statusInfoRepository.findByUser(user)
                .orElseThrow(() -> new NoSuchElementException("유저 상태 정보가 존재하지 않습니다."));
    }


}
