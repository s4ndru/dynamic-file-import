package transformation;

/**
 * Created by s4ndru on 08/08/2016.
 *
 * Gets thrown when definition of some property/field is wrong (e.g. class name given as string but class cannot be found in DB/application)
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
