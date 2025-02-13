package sookmyung.noonsongmaker.Service.auth;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import sookmyung.noonsongmaker.Dto.auth.LoginRequestDto;
import sookmyung.noonsongmaker.Dto.auth.SignupRequestDto;
import sookmyung.noonsongmaker.Entity.User;
import sookmyung.noonsongmaker.Repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import sookmyung.noonsongmaker.jwt.JwtProvider;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final StringRedisTemplate redisTemplate;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final RefreshTokenService refreshTokenService;

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

    public String login(LoginRequestDto loginRequestDto, HttpServletResponse response) {
        String email = loginRequestDto.getEmail();
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, loginRequestDto.getPassword())
        );

        String accessToken = jwtProvider.generateAccessToken(email);
        String refreshToken = jwtProvider.generateRefreshToken(email);

        long expiration = jwtProvider.getExpiration(refreshToken);
        refreshTokenService.saveRefreshToken(email, refreshToken, expiration);

        setCookie(response, "ACCESS_TOKEN", accessToken);
        setCookie(response, "REFRESH_TOKEN", refreshToken);

        return email;
    }

    private void setCookie(HttpServletResponse response, String name, String value) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
    }
}
