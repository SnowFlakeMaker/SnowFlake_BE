package sookmyung.noonsongmaker.jwt;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER) // 매개변수에서만 사용
@Retention(RetentionPolicy.RUNTIME) // 어노테이션이 런타임 동안 유지
@AuthenticationPrincipal // @CurrentUser 가 붙은 매개변수에 현재 인증된 사용자 객체 주입
public @interface CurrentUser {
}
