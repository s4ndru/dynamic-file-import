package extraction

/**
 * Created by s4ndru on 04/04/2016.
 */
enum AllowedFileType {
    TXT(".txt"), CSV(".csv"), JSON(".json"), XML(".xml"), PRN(".prn"), YAML(".yaml")

    String propertyName

    AllowedFileType(String propertyName) {
        this.propertyName = propertyName
    }

    String toString(){
        return propertyName
    }

    static AllowedFileType fromString(String text) {
        if (text != null) {
            for (AllowedFileType type : values()) {
                if (text.equalsIgnoreCase(type.propertyName)) {
                    return type
                }
            }
        }
        return null
    }
}

