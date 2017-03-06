package extraction

class CreateTagSelectParserTagLib {

    static namespace="dfi"

    def CreateTagSelectParser = {
        out << g.render(template: "/extraction/createTagSelectParser")
    }
}
