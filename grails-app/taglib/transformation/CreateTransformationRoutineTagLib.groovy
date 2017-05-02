package transformation

class CreateTransformationRoutineTagLib {
    static namespace="dfi"

    def CreateTransformationRoutine = {

        def domainList = grailsApplication.getArtefacts("Domain")*.clazz

        // TODO only remove our parsing stuff, not everything from those packages.
        domainList.removeAll{it.name.contains("extraction") || it.name.contains("transformation")}
        params.domainList = []
        domainList.each{params.domainList.add(it.name)}

        out << g.render(template: "/transformation/createTransformationRoutine", params: params)
    }
}
