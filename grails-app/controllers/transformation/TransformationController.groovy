package transformation

import extraction.DynamicParser


class TransformationController {

    def setProcedureProperties(){
        //TODO
        TransformationRoutine routine = TransformationRoutine.get(Integer.parseInt((String)params.routine))

        // We get all parsers, under which this routine exists.
        // This means a routine with its procedures could be applied for different parsers which deliver the same result.
        List<DynamicParser> parsers = DynamicParser.withCriteria{
            routines{
                idEq(routine.id)
            }
        }

        params.entries = parsers.entries.field.flatten()
        params.wrapperCount = MethodInfo.getWrapperCount(MethodInfo.fromString((String)params["method"]))

        params.method = params["method"]

        for(int i = 0; i < (Integer)params.wrapperCount; i++)
            if(MethodInfo.isFromObject((String)params["method"], i))
            // TODO: might be a problem if we work with the short name and not the full name, when finding our target_object
                params.objectFields = grailsApplication.getArtefacts("Domain").find{it.shortName == routine.target_object}.persistantProperties.name
            else if(MethodInfo.isDatum((String)params["method"], i))
                params.dataFields = params.entries

        // TODO: special stuff for createNewTemporaryProcedure, if yes, find out the type of the parameters
        render(template: "createTransformationProcedureSubElements", params: params)
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

    // TODO: catch already given sequencenumber
    def createTransformationProcedure(){

        TransformationProcedure tp = new TransformationProcedure()
        tp.properties = params

        //tp.save(flush: true)

        return
    }
}
