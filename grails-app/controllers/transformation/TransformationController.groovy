package transformation

import extraction.DynamicParser


class TransformationController {

    def createProcedure() {
        /*LinkedHashMap<Long, String> routines = [:]

        TransformationRoutine.getAll().eachWithIndex{ it, index ->
            def parser = DynamicParser.find("from DynamicParser where ? in elements(routines)", [it])
            routines.put(it.id, "Routine #" + it.order_id + " for '" + it.target_object + "' in parser '" + parser.name + "'")
        }

        params.routines = routines*/

        render(view: "/testing/procedure_test", params: params)
    }

    def createRoutine() {
        def domainList = grailsApplication.getArtefacts("Domain")*.clazz

        // TODO only remove our parsing stuff, not everything from those packages.
        domainList.removeAll{it.name.contains("extraction") || it.name.contains("transformation")}
        params.domainList = []
        domainList.each{params.domainList.add(it.name)}

        render(view: "/testing/routine_test", params: params)
    }

    def setRoutineProperties(){
        DynamicParser parser = DynamicParser.get(Integer.parseInt((String)params.parser))
        params.entries = parser.entries.field

        params.properties = grailsApplication.getArtefacts("Domain").find{it.fullName == params.targetObject}.persistantProperties
        params.properties = params.properties.name

        params.propertyCounter = 1

        render(template: "createTransformationRoutineSubElements", params: params)
    }

    def addPropertyPair(){
        params.propertyCounter = Integer.parseInt((String)params.propertyCounter) + 1

        DynamicParser parser = DynamicParser.get(Integer.parseInt((String)params.belongs_to))
        params.entries = parser.entries.field

        params.properties = grailsApplication.getArtefacts("Domain").find{it.fullName == params.target_object}.persistantProperties
        params.properties = params.properties.name

        int i = 0
        while(params["update_key" + i]) {
            params["update_key" + i] = params["update_key" + i]
            params["update_value" + i] = params["update_value" + i]
            i++
        }

        render(template: "transformationRoutineProperties", params: params)
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
            response.sendError(400, "Sorry, a target object: '" + (String)params["target_object"] + "' does not exist! Please go back and reconsider the targetobject parameter.")
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
