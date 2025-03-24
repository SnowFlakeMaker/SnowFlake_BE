package sookmyung.noonsongmaker.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sookmyung.noonsongmaker.Dto.Response;
import sookmyung.noonsongmaker.Dto.course.CreditResponseDto;
import sookmyung.noonsongmaker.Dto.ending.EndingResponseDto;
import sookmyung.noonsongmaker.Entity.User;
import sookmyung.noonsongmaker.Service.ending.EndingService;
import sookmyung.noonsongmaker.jwt.CurrentUser;

import java.util.Map;

@RestController
@RequestMapping("/ending")
@RequiredArgsConstructor
public class EndingController {
    private final EndingService endingService;

    @GetMapping("/final")
    public ResponseEntity<Response<EndingResponseDto>> getEnding(@CurrentUser User user) {
        EndingResponseDto response = endingService.getEnding(user);

        return ResponseEntity.ok(Response.buildResponse(response, "엔딩"));
    }
}
