package extraction

class CreateSimpleTagParserTagLib {

    static namespace="dim"

    def createSimpleTagParser = {
        out << g.javascript(library: "jquery", plugin: "jquery")
        out << g.render(template: "/extraction/createSimpleTagParser", plugin: "dynamic-import-module")
    }
}
