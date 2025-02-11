package sookmyung.noonsongmaker.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Response<T> {
    private String message; // 메시지
    private T data;         // 실제 데이터

    public static <T> Response<T> buildResponse(T data, String message) {
        return new Response<>(message, data);
    }

}