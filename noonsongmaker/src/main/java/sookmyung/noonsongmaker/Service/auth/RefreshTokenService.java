package sookmyung.noonsongmaker.Service.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import sookmyung.noonsongmaker.jwt.JwtProvider;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final StringRedisTemplate stringRedisTemplate;
    private final JwtProvider jwtProvider;
    private static final String BLACKLIST_PREFIX = "BLACKLIST_";

    public void saveRefreshToken(String email, String refreshToken, long expiration) {
        stringRedisTemplate.opsForValue().set(email, refreshToken, expiration, TimeUnit.MILLISECONDS);
    }

    public boolean validateRefreshToken(String email, String refreshToken) {
        String storedRefreshToken = stringRedisTemplate.opsForValue().get(email);
        String blacklist = stringRedisTemplate.opsForValue().get(BLACKLIST_PREFIX + refreshToken);
        return storedRefreshToken != null && storedRefreshToken.equals(refreshToken) && blacklist == null;
    }

    public void invalidateRefreshToken(String email, String refreshToken) {
        stringRedisTemplate.delete(email);
        long expiration = jwtProvider.getExpiration(refreshToken);
        stringRedisTemplate.opsForValue().set(BLACKLIST_PREFIX + refreshToken, "LOGOUT", expiration, TimeUnit.MILLISECONDS);
    }
}
