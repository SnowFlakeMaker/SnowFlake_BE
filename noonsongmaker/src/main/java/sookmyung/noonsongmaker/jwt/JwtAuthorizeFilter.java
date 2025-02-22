package sookmyung.noonsongmaker.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.filter.OncePerRequestFilter;
import sookmyung.noonsongmaker.Entity.User;
import sookmyung.noonsongmaker.Repository.UserRepository;

import java.io.IOException;
import java.util.List;

@Slf4j
public class JwtAuthorizeFilter extends OncePerRequestFilter {
    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;

    public JwtAuthorizeFilter(JwtProvider jwtProvider, UserRepository userRepository) {
        this.jwtProvider = jwtProvider;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        log.info("📌 [JwtAuthorizeFilter] 요청 URL: {}", request.getRequestURI());

        String jwt = jwtProvider.resolveToken(request);
        log.info("📌 [JwtAuthorizeFilter] 추출된 JWT: {}", jwt);

        if (jwt != null && jwtProvider.validateToken(jwt)) {
            log.info("✅ [JwtAuthorizeFilter] JWT가 유효함");
            String email = jwtProvider.getEmailFromToken(jwt);
            log.info("📌 [JwtAuthorizeFilter] JWT에서 추출한 이메일: {}", email);

            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + email));

            log.info("✅ [JwtAuthorizeFilter] 데이터베이스에서 조회된 사용자: {} (ID: {})", user.getEmail(), user.getId());

            UserDetailsImpl userDetails = new UserDetailsImpl(user);
            log.info("✅ [JwtAuthorizeFilter] SecurityContext에 저장된 인증 객체: {}", SecurityContextHolder.getContext().getAuthentication());

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userDetails, null, List.of());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.info("✅ [JwtAuthorizeFilter] SecurityContext에 저장된 사용자: {}", user.getEmail());
        }

        filterChain.doFilter(request, response);
    }
}
