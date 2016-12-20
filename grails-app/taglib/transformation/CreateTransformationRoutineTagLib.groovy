package transformation

class CreateTransformationRoutineTagLib {
    static namespace="dfi"

    def CreateTransformationRoutine = {
        out << g.render(template: "/transformation/createTransformationRoutine")
    }
}
