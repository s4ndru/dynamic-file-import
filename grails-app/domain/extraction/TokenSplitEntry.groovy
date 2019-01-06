package extraction

// The TokenSplitEntry represents an line/object in a file, for the TokenSplitParser
class TokenSplitEntry extends DynamicParserEntry{

    Integer splitIndex

    static constraints = {
        splitIndex(nullable: false, min: 0)
    }

}
