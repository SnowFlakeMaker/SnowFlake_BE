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

    public void saveRefreshToken(String email, String refreshToken, long expiration) {
        stringRedisTemplate.opsForValue().set(email, refreshToken, expiration, TimeUnit.MILLISECONDS);
    }

    public boolean validateRefreshToken(String email, String refreshToken) {
        String storedRefreshToken = stringRedisTemplate.opsForValue().get(email);
        return storedRefreshToken != null && storedRefreshToken.equals(refreshToken);
    }

    public void invalidateRefreshToken(String email, String refreshToken) {
        stringRedisTemplate.delete(email);
        long expiration = jwtProvider.getExpiration(refreshToken);
        stringRedisTemplate.opsForValue().set("BLACKLIST_" + refreshToken, "LOGOUT", expiration, TimeUnit.MILLISECONDS);
    }
}
