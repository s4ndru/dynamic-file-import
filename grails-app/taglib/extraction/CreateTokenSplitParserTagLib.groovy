package extraction

class CreateTokenSplitParserTagLib {

    static namespace="dfi"

    def createTokenSplitParser = {
        out << g.render(template: "/extraction/createTokenSplitParser")
    }
}
