package transformation;

/**
 * Created by s4ndru on 08/08/2016.
 */
public class IncorrectSpecificationException extends Exception{

    public IncorrectSpecificationException() {}

    public IncorrectSpecificationException(String message) {
        super(message);
    }

    public IncorrectSpecificationException(String message, Throwable cause) {
        super(message, cause);
    }

    public IncorrectSpecificationException(Throwable cause) {
        super(cause);
    }

    public IncorrectSpecificationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
