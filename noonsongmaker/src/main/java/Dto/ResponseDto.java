package Dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResponseDto<T> {
    private String message; // 메시지
    private T data;         // 실제 데이터

    public static <T> ResponseDto<T> buildResponse(T data, String message) {
        return new ResponseDto<>(message, data);
    }

}