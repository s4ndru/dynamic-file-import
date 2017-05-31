package extraction

class SimpleTagEntry extends DynamicParserEntry{

    String startTag
    String endTag = null

    static constraints = {
        startTag(blank: false, nullable: false)
        endTag(blank: false, nullable: true)
    }
}
