package extraction;

/**
 * Created by s4ndru on 04/04/2016.
 */
enum EntryDatatype {
    STRING("String"), INTEGER("Integer"), FLOAT("Float") //, DATE("date")

    String propertyName

    EntryDatatype(String propertyName) {
        this.propertyName = propertyName
    }

    String toString(){
        return propertyName
    }

    static EntryDatatype fromString(String text) {
        if (text != null) {
            for (EntryDatatype type : values()) {
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
