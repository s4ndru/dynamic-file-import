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

    def setProcedureProperties(){
        //TODO
        DynamicParser parser = DynamicParser.get(Integer.parseInt((String)params.parser))
        params.entries = parser.entries.field

        MethodInfo.fromString((String)params["method"])
        params.wrapperCount = MethodInfo.getWrapperCount(MethodInfo.fromString((String)params["method"]))

        render(template: "createTransformationProcedureSubElements", params: params)
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
            DynamicParser parser = DynamicParser.get(Integer.parseInt((String)params["belongs_to"]))

            if(parser.routines.order_id.contains(Integer.parseInt((String)params["order_id"]))){

                StringBuilder sequenceNumbersString = new StringBuilder()
                parser.routines.order_id.each{
                    sequenceNumbersString.append(it)
                    sequenceNumbersString.append(", ")
                }
                sequenceNumbersString.delete(sequenceNumbersString.length() - 2, sequenceNumbersString.length())

                render(status: 400, text: "Sorry, the sequence numbers '" + sequenceNumbersString.toString() + "' are already taken! Please input a number unequal to the others.")
                return
            }

            TransformationRoutine tr = new TransformationRoutine()
            Class temp = TransformationService.getClassFromString((String)params["target_object"])

            tr.properties = params

            if(params["to_update"]){
                int i = 0
                while (params["update_key" + i] && params["update_key" + i] != "null" && params["update_value" + i] && params["update_value" + i] != "null") {
                    if (temp.declaredFields.collect().find { it.name == params["update_key" + i] } == null) {
                        //response.sendError(400, "Sorry, the class '" + (String) params["target_object"] + "' does not have a property '" + (String) params["update_key" + i] + "'! Please go back and reconsider the update property parameter(s).")
                        render(status: 400, text: "Sorry, the class '" + (String) params["target_object"] + "' does not have a property '" + (String) params["update_key" + i] + "'! Please go back and reconsider the update property parameter(s).")
                        return
                    }
                    tr.update_properties.put((String) params["update_key" + i], (String) params["update_value" + i])

                    //params["update_key" + i] = (String) params["update_key" + i]
                    //params["update_value" + i] = (String) params["update_value" + i]

                    i++
                }
                if(tr.update_properties.size() == 0) {
                    render(status: 400, text: "There are no update properties defined, even though the 'Update existing'-checkbox is checked!")
                    return
                }

                //params["propertyCounter"] = i
            }
            /*else{
                //params["propertyCounter"] = 0
            }*/

            tr.save(flush: true)

            parser.routines.add(tr)
            parser.save(flush: true)
        }
        catch(IncorrectSpecificationException e) {
            render(status: 400, text: "Sorry, a target object: '" + (String)params["target_object"] + "' is not valid! Please go back and reconsider the target-object selection.")
            //redirect(url: request.getHeader('referer'), params: params)
            return
        }

        render(status: 200)
    }

    def createTransformationProcedure(){

        TransformationProcedure tp = new TransformationProcedure()
        tp.properties = params

        //tp.save(flush: true)

        return
    }
}
