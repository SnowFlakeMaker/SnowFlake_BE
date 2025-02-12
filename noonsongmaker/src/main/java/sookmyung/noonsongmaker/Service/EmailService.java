package sookmyung.noonsongmaker.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import sookmyung.noonsongmaker.Dto.auth.MailSendDto;
import sookmyung.noonsongmaker.Dto.auth.VerificationRequestDto;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final StringRedisTemplate redisTemplate;

    private static final String EMAIL_PREFIX = "EMAIL_VERIFICATION_";
    private static final long CODE_EXPIRE_TIME = 5L;

    public void sendEmail(MailSendDto mailSendDto) {
        try {
            String email = mailSendDto.getEmailId() + "@sookmyung.ac.kr";
            String verificationCode = generateCode();

            redisTemplate.opsForValue().set(EMAIL_PREFIX + mailSendDto.getEmailId(), verificationCode, CODE_EXPIRE_TIME, TimeUnit.MINUTES);

            Context context = new Context();
            context.setVariable("verificationCode", verificationCode);

            String htmlContent = templateEngine.process("email", context);

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, "UTF-8");
            mimeMessageHelper.setTo(email);
            mimeMessageHelper.setSubject("눈송이메이커 회원 가입 이메일 인증 코드");
            mimeMessageHelper.setText(htmlContent, true);
            mailSender.send(mimeMessage);

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    public String generateCode() {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        return uuid.substring(0, 6).toUpperCase();
    }

}
