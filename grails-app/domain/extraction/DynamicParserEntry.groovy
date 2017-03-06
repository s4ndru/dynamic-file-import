package extraction

// The FileEntryParser represents an Entry in a File, for the FileParser
abstract class DynamicParserEntry{

    String field
    boolean optional = false
    boolean trim = false
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
        else if(dataType == EntryDatatype.STRING){
            return true
        }

        return false
    }
}
