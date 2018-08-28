package transformation

import extraction.DynamicParser


class TransformationController {

    def setRoutineProperties(){
//        if(params.parser == "null"){
//            render(status: 400, text: "Please select a parser from the 'Parser' dropdown!")
//            return
//        }
//        if(params.targetObject == "null"){
//            render(status: 400, text: "Please select a target object from the 'Object' dropdown!")
//            return
//        }

        DynamicParser parser = DynamicParser.get(Integer.parseInt((String)params.parser))
        params.entries = parser.entries.field.sort()

        params.properties = grailsApplication.getArtefacts("Domain").find{it.fullName == params.targetObject}.persistantProperties
        params.properties = params.properties.name.sort()

        params.propertyCounter = 1

        render(template: "createTransformationRoutineSubElements", params: params)
    }

    def setProcedureProperties(){
        //TODO finish this

//        if(params.routine == "null"){
//            render(status: 400, text: "Please select a routine from the 'Routine' dropdown!")
//            return
//        }
//        if(params.method == "null"){
//            render(status: 400, text: "Please select a method from the 'Methodname' dropdown!")
//            return
//        }


        TransformationRoutine routine = TransformationRoutine.get(Integer.parseInt((String)params.routine))

        // We get all parsers, under which this routine exists.
        // This means a routine with its procedures could be applied for different parsers which deliver the same result.
        List<DynamicParser> parsers = DynamicParser.withCriteria{
            routines{
                idEq(routine.id)
            }
        }

        params.entries = parsers.entries.field.flatten().sort()
        params.wrapperCount = MethodInfo.getWrapperCount(MethodInfo.fromString((String)params["method"]))
        params.method = params.method

        for(int i = 0; i < (Integer)params.wrapperCount; i++)
            if(MethodInfo.isFromObject((String)params["method"], i))
                params.objectFields = grailsApplication.getArtefacts("Domain").find{it.fullName == routine.target_object}.persistantProperties.name.sort()
            else if(MethodInfo.isDataWrapper((String)params["method"], i))
                params.objectFields = params.entries

        // TODO: special stuff for createNewTemporaryProcedure. If it is "createNewTemporaryProcedure", find out the type of the parameters
        render(template: "createTransformationProcedureSubElements", params: params)
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

            if(!params["order_id"] || params["order_id"] == "null"){
                render(status: 400, text: "Sequence number has to be set!")
                return
            }

            if(parser.routines.order_id.contains(Integer.parseInt((String)params["order_id"]))){

                StringBuilder sequenceNumbersString = new StringBuilder()
                parser.routines.order_id.each{
                    sequenceNumbersString.append(it)
                    sequenceNumbersString.append(", ")
                }
                sequenceNumbersString.delete(sequenceNumbersString.length() - 2, sequenceNumbersString.length())

                render(status: 400, text: "The sequence numbers '" + sequenceNumbersString.toString() + "' are already taken! Please input a number unequal to the others.")
                return
            }

            TransformationRoutine tr = new TransformationRoutine()
            Class temp_class = TransformationService.getClassFromString((String)params["target_object"])

            tr.properties = params

            if(params["to_update"]){
                int i = 0
                while (params["update_key" + i] && params["update_key" + i] != "null" && params["update_value" + i] && params["update_value" + i] != "null") {

                    // Problematic if field has a name which is predefined in grails. e.g. constraints, errors, log
                    if (temp_class.declaredFields.collect().find { it.name == params["update_value" + i] } == null) {
                        render(status: 400, text: "The class '" + (String) params["target_object"] + "' does not have a property '" + (String) params["update_value" + i] + "'! Please go back and reconsider the update property parameter(s).")
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
            else{
                while(params["update_key" + 0] && params["update_key" + 0] != "null" && params["update_value" + 0] && params["update_value" + 0] != "null") {
                    render(status: 400, text: "Update properties were set even though 'Update existing'-checkbox was not set! Please rethink if you actually want to update existing objects.")
                    return
                }
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

    // TODO: catch already given sequencenumber
    def createTransformationProcedure(){

        TransformationProcedure tp = new TransformationProcedure()
        tp.properties = params

        //tp.save(flush: true)

        return
    }
}
