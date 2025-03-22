package sookmyung.noonsongmaker.Service.auth;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseCookie;
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

import java.time.Duration;

@Service
@RequiredArgsConstructor
@Slf4j
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

        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        }

        Boolean isVerified = redisTemplate.hasKey(EMAIL_PREFIX + email);
        if (!Boolean.TRUE.equals(isVerified)) {
            throw new IllegalArgumentException("이메일 인증이 필요합니다.");
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
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, loginRequestDto.getPassword())
            );

            String accessToken = jwtProvider.generateAccessToken(email);
            String refreshToken = jwtProvider.generateRefreshToken(email);

            long expiration = jwtProvider.getExpiration(refreshToken);
            refreshTokenService.saveRefreshToken(email, refreshToken, expiration);

            setCookie(response, "ACCESS_TOKEN", accessToken, 60 * 60);
            setCookie(response, "REFRESH_TOKEN", refreshToken, (int) expiration);

            return email;
        } catch (org.springframework.security.authentication.BadCredentialsException e) {
            throw new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다.");
        }
    }

    public String refreshAccessToken(String refreshToken, HttpServletResponse response) {
        log.info("[refreshAccessToken] Called with token: {}", refreshToken);

        if (!jwtProvider.validateToken(refreshToken)) {
            log.warn("[refreshAccessToken] Invalid refresh token");
            throw new IllegalArgumentException("유효하지 않은 Refresh Token");
        }

        String email = jwtProvider.getEmailFromToken(refreshToken);
        log.info("[refreshAccessToken] Email extracted from token: {}", email);

        if (!refreshTokenService.validateRefreshToken(email, refreshToken)) {
            log.warn("[refreshAccessToken] Refresh token invalid or expired for email: {}", email);
            throw new IllegalArgumentException("만료되었거나 사용 불가능한 Refresh Token");
        }

        String newAccessToken = jwtProvider.generateAccessToken(email);
        log.info("[refreshAccessToken] New access token issued for email: {}", email);
        setCookie(response, "ACCESS_TOKEN", newAccessToken, 60 * 60);
        return email;
    }

    private void setCookie(HttpServletResponse response, String name, String value, int maxAgeSeconds) {
        ResponseCookie cookie = ResponseCookie.from(name, value)
                .path("/")
                .httpOnly(true)
                .secure(false) // Set to true in production
                .maxAge(Duration.ofSeconds(maxAgeSeconds))
                .sameSite("Lax")
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
    }

    public void logout(String refreshToken, HttpServletResponse response) {
        String email = jwtProvider.getEmailFromToken(refreshToken);

        refreshTokenService.invalidateRefreshToken(email, refreshToken);

        deleteCookie(response, "ACCESS_TOKEN");
        deleteCookie(response, "REFRESH_TOKEN");
    }

    private void deleteCookie(HttpServletResponse response, String name) {
        Cookie cookie = new Cookie(name, null);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}
