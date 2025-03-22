package sookmyung.noonsongmaker.Exception;

public class StressOverflowException extends RuntimeException {
    public StressOverflowException() {
        super("스트레스 수치가 100에 도달했습니다.");
    }
}
