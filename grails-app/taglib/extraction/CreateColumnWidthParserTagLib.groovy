package extraction

class CreateColumnWidthParserTagLib {

    static namespace="dfi"

    def createColumnWidthParser = {
        out << g.render(template: "/extraction/createColumnWidthParser")
//        out << g.javascript()
    }
}
