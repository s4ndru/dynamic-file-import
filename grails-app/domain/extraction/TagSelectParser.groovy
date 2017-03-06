package extraction

class TagSelectParser extends DynamicParser{

    boolean is_xml = false
    String objectStartTag
    String objectEndTag

    static constraints = {
        name(blank: false)
        objectStartTag(nullable: true)
        objectEndTag(nullable: true)
    }

    @Override
    ArrayList<Map<String, String>> parse(File file) throws ParserUnfitException {
        if(is_xml)
        {
            new XmlSlurper().parse(file)
        }

        return null
    }

    String toString() {
        "Tagparser for " + name
    }
}
