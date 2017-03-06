package extraction

/**
 * Created by s4ndru on 04/04/2016.
 */
enum AllowedFiletype {
    TXT(".txt"), CSV(".csv")//, XML(".xml")

    String propertyName

    AllowedFiletype(String propertyName) {
        this.propertyName = propertyName
    }

    String toString(){
        return propertyName
    }

    static AllowedFiletype fromString(String text) {
        if (text != null) {
            for (AllowedFiletype type : values()) {
                if (text.equalsIgnoreCase(type.propertyName)) {
                    return type
                }
            }
        }
        return null
    }
}

