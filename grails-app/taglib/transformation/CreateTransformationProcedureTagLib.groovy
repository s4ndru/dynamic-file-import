package transformation

class CreateTransformationProcedureTagLib {
    static namespace="dim"

    def CreateTransformationProcedure = {
        out << g.javascript(src: "/jquery-1.11.1.js", plugin: "dynamic-import-module")
        out << g.render(template: "/transformation/createTransformationProcedure", plugin: "dynamic-import-module", params: params)
    }
}
