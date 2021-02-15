package extraction

class CreateTokenSplitParserTagLib {

    static namespace="dim"

    def createTokenSplitParser = {
        out << g.javascript(src: "/jquery-1.11.1.js", plugin: "dynamic-import-module")
        out << g.render(template: "/extraction/createTokenSplitParser", plugin: "dynamic-import-module")
    }
}
