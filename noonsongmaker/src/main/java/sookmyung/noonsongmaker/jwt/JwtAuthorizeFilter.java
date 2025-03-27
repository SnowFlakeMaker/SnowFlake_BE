package sookmyung.noonsongmaker.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.filter.OncePerRequestFilter;
import sookmyung.noonsongmaker.Entity.User;
import sookmyung.noonsongmaker.Repository.UserRepository;

import java.io.IOException;
import java.util.List;

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


        String jwt = jwtProvider.resolveToken(request);

        if (jwt != null && jwtProvider.validateToken(jwt)) {
            String email = jwtProvider.getEmailFromToken(jwt);

            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + email));


            UserDetailsImpl userDetails = new UserDetailsImpl(user);

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userDetails, null, List.of());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }
}
