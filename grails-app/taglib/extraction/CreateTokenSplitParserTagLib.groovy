package extraction

class CreateTokenSplitParserTagLib {

    static namespace="dfi"

    def CreateTokenSplitParser = {
        out << g.render(template: "/extraction/createTokenSplitParser")
    }
}
