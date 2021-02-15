package extraction

class CreateSimpleTagParserTagLib {

    static namespace="dim"

    def createSimpleTagParser = {
        out << g.javascript(src: "/jquery-1.11.1.js", plugin: "dynamic-import-module")
        out << g.render(template: "/extraction/createSimpleTagParser", plugin: "dynamic-import-module")
    }
}
