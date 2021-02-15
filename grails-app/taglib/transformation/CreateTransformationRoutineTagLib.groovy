package transformation

class CreateTransformationRoutineTagLib {
    static namespace="dim"

    def CreateTransformationRoutine = {

        def domainList = grailsApplication.getArtefacts("Domain")*.clazz

        // TODO Test this in another application if we correctly remove only our stuff
        domainList.removeAll{it.name.split("\\.")[0].contains("extraction") || it.name.split("\\.")[0].contains("transformation")}
        params.domainList = []
        domainList.each{params.domainList.add(it.name)}

        out << g.javascript(src: "/jquery-1.11.1.js", plugin: "dynamic-import-module")
        out << g.render(template: "/transformation/createTransformationRoutine", plugin: "dynamic-import-module", params: params)
    }
}
