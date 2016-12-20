package transformation

class CreateTransformationProcedureTagLib {
    static namespace="dfi"

    def CreateTransformationProcedure = {
        out << g.render(template: "/transformation/createTransformationProcedure")
    }
}
