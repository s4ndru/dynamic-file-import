package extraction

/**
 * Created by s4ndru on 04/04/2016.
 */
public enum AllowedFiletype {
    TXT(".txt"), CSV(".csv")//, XML(".xml")

    String propertyName

    public AllowedFiletype(String propertyName) {
        this.propertyName = propertyName
    }

    String toString(){
        return propertyName
    }

    public static AllowedFiletype fromString(String text) {
        if (text != null) {
            for (AllowedFiletype type : AllowedFiletype.values()) {
                if (text.equalsIgnoreCase(type.propertyName)) {
                    return type;
                }
            }
        }
        return null;
    }
}

