package extraction

class CreateColumnWidthParserTagLib {

    static namespace="dim"

    def createColumnWidthParser = {
        out << g.javascript(src: "/jquery-1.11.1.js", plugin: "dynamic-import-module")
        out << g.render(template: "/extraction/createColumnWidthParser", plugin: "dynamic-import-module")
    }
}
