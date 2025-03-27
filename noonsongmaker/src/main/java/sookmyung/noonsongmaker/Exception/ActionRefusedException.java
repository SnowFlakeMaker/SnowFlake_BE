package sookmyung.noonsongmaker.Exception;

public class ActionRefusedException extends RuntimeException {
    private final Object data;

    public ActionRefusedException(String message, Object data) {
        super(message);
        this.data = data;
    }

    public Object getData() {
        return data;
    }
}