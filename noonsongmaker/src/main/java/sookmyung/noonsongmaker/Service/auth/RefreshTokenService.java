package sookmyung.noonsongmaker.Service.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final StringRedisTemplate stringRedisTemplate;

    private static final long REFRESH_TOKEN_EXPIRE = 60 * 60 * 24 * 7;
    private final RedisTemplate<String, Object> redisTemplate;

    public void saveRefreshToken(String email, String refreshToken) {
        redisTemplate.opsForValue().set(email, refreshToken, REFRESH_TOKEN_EXPIRE, TimeUnit.SECONDS);
    }

    public boolean validateRefreshToken(String email, String refreshToken) {
        String storedRefreshToken = stringRedisTemplate.opsForValue().get(email);
        return storedRefreshToken != null && storedRefreshToken.equals(refreshToken);
    }

    public void invalidateRefreshToken(String email) {
        stringRedisTemplate.delete(email);
    }
}
