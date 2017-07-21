package extraction;

/**
 * Created by s4ndru on 23/03/2016.
 * Exception in case the parser cannot parse a file the user defined for it.
 */

public class ParserUnfitException extends Exception {

    public ParserUnfitException() {}

    public ParserUnfitException(String message) {
        super(message);
    }

    public ParserUnfitException(String message, Throwable cause) {
        super(message, cause);
    }

    public ParserUnfitException(Throwable cause) {
        super(cause);
    }

    public ParserUnfitException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
