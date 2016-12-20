package extraction

class FilesSelectionTagLib {

    static namespace="dfi"

    def fileSelection = {
        out << g.form(action: "parseFiles", controller: "extraction", name: "dfiParsingForm", {
            "<input type='Text' id='filesPath' name='filesPath'/>" +
            g.actionSubmit(action: "parseFiles", value: "parse files")})

//        out << "<input type='file' id='fileSelect' name='fileSelect' multiple='true' accept='.csv,.txt,.xml'/>"
//        out << g.actionSubmit(action: "parseFiles", value: "parse files")
    }
}
