package transformation;

/**
 * Created by Sandr on 30.05.2017.
 *
 * Thrown when an object could not be committed to a database for whatever reason
 */
public class DatabaseSaveException extends Exception{
    public DatabaseSaveException() {}

    public DatabaseSaveException(String message) {
        super(message);
    }

    public DatabaseSaveException(String message, Throwable cause) {
        super(message, cause);
    }

    public DatabaseSaveException(Throwable cause) {
        super(cause);
    }

    public DatabaseSaveException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
