package sookmyung.noonsongmaker.Service;

import org.springframework.data.redis.core.StringRedisTemplate;
import sookmyung.noonsongmaker.Dto.auth.SignupRequestDto;
import sookmyung.noonsongmaker.Entity.User;
import sookmyung.noonsongmaker.Repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final StringRedisTemplate redisTemplate;

    private static final String EMAIL_PREFIX = "EMAIL_VERIFIED_";

    @Transactional
    public void signUp(SignupRequestDto signupRequestDto) {
        String email = signupRequestDto.getEmail();

        Boolean isVerified = redisTemplate.hasKey(EMAIL_PREFIX + email);
        if (!isVerified) {
            throw new IllegalArgumentException("이메일 인증이 필요합니다.");
        }

        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already in use");
        }

        User user = User.builder()
                .email(email)
                .password(passwordEncoder.encode(signupRequestDto.getPassword()))
                .build();

        userRepository.save(user);

        redisTemplate.delete(EMAIL_PREFIX + email);
    }
}
