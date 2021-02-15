package extraction

class CreateSimpleXMLParserTagLib {

    static namespace="dim"

    def createSimpleXMLParser = {
        out << g.javascript(src: "/jquery-1.11.1.js", plugin: "dynamic-import-module")
        out << g.render(template: "/extraction/createSimpleXMLParser", plugin: "dynamic-import-module")
    }
}

