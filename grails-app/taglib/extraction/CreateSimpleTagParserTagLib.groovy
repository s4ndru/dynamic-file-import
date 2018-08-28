package extraction

class CreateSimpleTagParserTagLib {

    static namespace="dfi"

    def createSimpleTagParser = {
        out << g.render(template: "/extraction/createSimpleTagParser")
    }
}
