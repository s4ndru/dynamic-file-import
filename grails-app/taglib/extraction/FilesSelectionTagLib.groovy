package extraction

class FilesSelectionTagLib {

    static namespace="dim"

    def fileSelection = {
        out << g.javascript(src: "/jquery-1.11.1.js", plugin: "dynamic-import-module")
        out << g.form(action: "parseFiles", controller: "extraction", name: "dfiParsingForm", {
            "<input type='Text' id='filesPath' name='filesPath'/>" +
            g.submitToRemote(action: "parseFiles", value: "parse files", controller: "extraction", onComplete: "alert(XMLHttpRequest.responseText); location.reload();")})
    }
}
