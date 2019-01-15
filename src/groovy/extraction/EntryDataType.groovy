package extraction

/**
 * Created by s4ndru on 04/04/2016.
 */
enum EntryDataType {
    STRING("String"), INTEGER("Integer"), FLOAT("Float"), BOOLEAN("Boolean"), LONG("Long") //, DATE("date")

    String propertyName

    EntryDataType(String propertyName) {
        this.propertyName = propertyName
    }

    String toString(){
        return propertyName
    }

    static EntryDataType fromString(String text) {
        if (text != null) {
            for (EntryDataType type : values()) {
                if (text.equalsIgnoreCase(type.propertyName)) {
                    return type
                }
            }
        }
        return null
    }

//    public static Date parseDate(){
//        Date.parse()
//    }
}
