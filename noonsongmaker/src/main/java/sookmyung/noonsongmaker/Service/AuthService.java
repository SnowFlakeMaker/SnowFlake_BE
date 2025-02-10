package sookmyung.noonsongmaker.Service;

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

    @Transactional
    public void signUp(SignupRequestDto signupRequestDto) {
        if (userRepository.existsByEmail(signupRequestDto.getEmail())) {
            throw new IllegalArgumentException("Email already in use");
        }

        User user = User.builder()
                .email(signupRequestDto.getEmail())
                .password(passwordEncoder.encode(signupRequestDto.getPassword()))
                .build();

        userRepository.save(user);
    }
}
