package extraction;

/**
 * Created by s4ndru on 06/04/2016.
 * Exception in case the parserdefinition contradicts itself.
 */
public class ParserInconsistentException extends Exception{

    public ParserInconsistentException() {}

    public ParserInconsistentException(String message) {
        super(message);
    }

    public ParserInconsistentException(String message, Throwable cause) {
        super(message, cause);
    }

    public ParserInconsistentException(Throwable cause) {
        super(cause);
    }

    public ParserInconsistentException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
