package transformation

class TransformationController {

    def createProcedure() {
        render(view: "/testing/procedure_test", params: params)
    }

    def createRoutine() {
        render(view: "/testing/routine_test", params: params)
    }

    def createTransformationRoutine(){
        try{
            TransformationRoutine tr = new TransformationRoutine()
            Class temp = TransformationService.getClassFromString((String)params["target_object"])

            tr.properties = params

            int i = 0
            while(params["update_key" + i] && params["update_value" + i]){
                if(temp.declaredFields.collect().find{it.name == params["update_key" + i]} == null){
                    response.sendError(400, "Sorry, the class '" + (String)params["target_object"] + "' does not have a property '" + (String)params["update_key" + i] + "'! Please go back and reconsider the update property parameter(s).")
                    return
                }
                tr.update_properties.put((String)params["update_key" + i], (String)params["update_value" + i])
            }

            tr.save(flush: true)
        }
        catch(IncorrectSpecificationException e) {
            response.sendError(400, "Sorry, a target object: '" + (String)params["target_object"] + "' does not exist! Please go back and reconsider the target object parameter.")
            //redirect(url: request.getHeader('referer'), params: params)
            return
        }

        return
    }

    def createTransformationProcedure(){

        TransformationProcedure tp = new TransformationProcedure()
        tp.properties = params

        //tp.save(flush: true)

        return
    }
}
