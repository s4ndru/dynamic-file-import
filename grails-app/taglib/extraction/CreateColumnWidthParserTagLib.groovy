package extraction

class CreateColumnWidthParserTagLib {

    static namespace="dim"

    def createColumnWidthParser = {
        out << g.javascript(library: "jquery", plugin: "jquery")
        out << g.render(template: "/extraction/createColumnWidthParser")
    }
}
