package extraction

// The FileEntryParser represents an Entry in a File, for the FileParser
abstract class DynamicParserEntry implements Comparable{

    String field
    boolean optional = false
    boolean trim = false // TODO remove this. Should be in transformation methods
    EntryDatatype dataType

    static constraints = {
        field(blank: false)
        dataType(nullable: false)
    }

    //abstract def parseForEntry(File File) throws ParseUnfitException

    String toString() {
        "Field ${field}"
    }

    Boolean checkType(String type) throws ParserUnfitException{
        if(dataType == EntryDatatype.INTEGER){
            try{
                Integer.parseInt(type)
                return true
            }
            catch(NumberFormatException e){
                if(optional)
                    return false

                throw new ParserUnfitException("Expected entry of type 'integer' in file", e.cause)
            }
        }
        if(dataType == EntryDatatype.LONG){
            try{
                Long.parseLong(type)
                return true
            }
            catch(NumberFormatException e){
                if(optional)
                    return false

                throw new ParserUnfitException("Expected entry of type 'integer' in file", e.cause)
            }
        }
        else if(dataType == EntryDatatype.FLOAT){
            try{
                Float.parseFloat(type)
                return true
            }
            catch(NumberFormatException e){
                if(optional)
                    return false

                throw new ParserUnfitException("Expected entry of type 'float' in file", e.cause)
            }
        }
        else if(dataType == EntryDatatype.BOOLEAN){
            try{
                Boolean.parseBoolean(type)
                return true
            }
            catch(NumberFormatException e){
                if(optional)
                    return false

                throw new ParserUnfitException("Expected entry of type 'boolean' in file", e.cause)
            }
        }
        else if(dataType == EntryDatatype.STRING){
            return true
        }

        return false
    }

    def parseField(String value){
        if(dataType == EntryDatatype.INTEGER){
            return Integer.parseInt(value)
        }
        else if(dataType == EntryDatatype.LONG){
            return Long.parseLong(value)
        }
        else if(dataType == EntryDatatype.FLOAT){
            return Float.parseFloat(value)
        }
        else if(dataType == EntryDatatype.BOOLEAN){
            return Boolean.parseBoolean(value)
        }
        else if(dataType == EntryDatatype.STRING){
            return value
        }
    }

    static mapping = {
        sort id: "asc"
    }

    int compareTo(obj) {
        id.compareTo((obj as DynamicParserEntry).id)
    }
}
