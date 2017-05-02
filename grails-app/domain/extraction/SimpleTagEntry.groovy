package extraction

class SimpleTagEntry extends DynamicParserEntry{

    String startTag
    String endTag = null
    boolean endTagOptional = false

    static constraints = {
        startTag(blank: false, nullable: false)
        endTag(blank: false, nullable: true)
    }
}
