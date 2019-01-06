package transformation

class CreateTransformationRoutineTagLib {
    static namespace="dfi"

    def CreateTransformationRoutine = {

        def domainList = grailsApplication.getArtefacts("Domain")*.clazz

        // TODO Test this in another application if we correctly remove only our stuff
        domainList.removeAll{it.name.split("\\.")[0].contains("extraction") || it.name.split("\\.")[0].contains("transformation")}
        params.domainList = []
        domainList.each{params.domainList.add(it.name)}

        out << g.render(template: "/transformation/createTransformationRoutine", params: params)
    }
}
