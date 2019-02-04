package extraction

class CreateSimpleXMLParserTagLib {

    static namespace="dim"

    def createSimpleXMLParser = {
        out << g.javascript(library: "jquery", plugin: "jquery")
        out << g.render(template: "/extraction/createSimpleXMLParser")
    }
}

