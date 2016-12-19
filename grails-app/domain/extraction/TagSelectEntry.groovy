package extraction
// The FileEntryParser represents an Entry in a File, for the FileParser
class TagSelectEntry extends DynamicParserEntry{

    String startTag
    String endTag
    boolean endTagOptional = false

    static constraints = {
        startTag(blank: false, nullable: false)
        endTag(nullable: true)
    }
}
