package extraction

// The TokenSplitEntry represents an line/object in a file, for the TokenSplitParser
class ColumnWidthEntry extends DynamicParserEntry{

    Integer columnStart
    Integer columnEnd

    static constraints = {
        columnStart(nullable: false)
        columnEnd(nullable: false)

        // TODO: ask gernot or roman for advice
        // optional(notEqual: true)
    }
}
