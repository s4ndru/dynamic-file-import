package extraction

class CreateColumnWidthParserTagLib {

    static namespace="dfi"

    def CreateColumnWidthParser = {
        out << g.render(template: "/extraction/createColumnWidthParser")
    }
}
