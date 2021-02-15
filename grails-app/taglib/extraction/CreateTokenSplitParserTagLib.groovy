package extraction

class CreateTokenSplitParserTagLib {

    static namespace="dim"

    def createTokenSplitParser = {
        out << g.javascript(library: "jquery", plugin: "jquery")
        out << g.render(template: "/extraction/createTokenSplitParser", plugin: "dynamic-import-module")
    }
}
