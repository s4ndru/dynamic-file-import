package extraction

class CreateSimpleXMLParserTagLib {

    static namespace="dfi"

    def createSimpleXMLParser = {
        out << g.render(template: "/extraction/createSimpleXMLParser")
    }
}

