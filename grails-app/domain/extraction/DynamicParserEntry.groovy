package extraction

// The FileEntryParser represents an Entry in a File, for the FileParser
// Comparable, so it is possible to parse more complex tags first, then simpler one later. E.g. "Timestamp:" & "Time"
class DynamicParserEntry implements Comparable{

    String field
    boolean optional = false
    EntryDataType dataType

    static constraints = {
        field(nullable: false, blank: false)
        dataType(nullable: false)
    }

    //abstract def parseForEntry(File File) throws ParseUnfitException

    DynamicParserEntry(String field, EntryDataType dataType){
        this.field = field
        this.dataType = dataType
        // This constructor is used when transformation procedures create entries which is then displayed in the UI. Optional is set to true to not trip and exceptions
        this.optional = true
    }

    String toString() {
        "Field ${field}"
    }

    Boolean checkType(String type) throws ParserUnfitException{
        if(dataType == EntryDataType.INTEGER){
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
        if(dataType == EntryDataType.LONG){
            try{
                Long.parseLong(type)
                return true
            }
            catch(NumberFormatException e){
                if(optional)
                    return false

                throw new ParserUnfitException("Expected entry of type 'long' in file", e.cause)
            }
        }
        else if(dataType == EntryDataType.FLOAT){
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
        else if(dataType == EntryDataType.BOOLEAN){
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
        else if(dataType == EntryDataType.STRING){
            return true
        }

        return false
    }

    def parseField(String value){
        if(dataType == EntryDataType.INTEGER){
            return Integer.parseInt(value)
        }
        else if(dataType == EntryDataType.LONG){
            return Long.parseLong(value)
        }
        else if(dataType == EntryDataType.FLOAT){
            return Float.parseFloat(value)
        }
        else if(dataType == EntryDataType.BOOLEAN){
            return Boolean.parseBoolean(value)
        }
        else if(dataType == EntryDataType.STRING){
            return value
        }
    }

    static mapping = {
        sort id: "asc"
    }

    @Override
    int compareTo(obj){
        id.compareTo(obj.id)
    }
}
