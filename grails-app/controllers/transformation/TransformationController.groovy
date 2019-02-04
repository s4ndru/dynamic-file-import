package transformation

import extraction.DynamicParser
import extraction.DynamicParserEntry

// TODO Doc note => Its possible to submit a method and then change the method in the drop down. Might be catched at runtime, might be not catched if functions are too similar

class TransformationController {
    static final String arithmeticPlaceholder = "INPUT OWN VALUE..."

    def setRoutineProperties() {
//        if(params.parser == "null"){
//            render(status: 400, text: "Please select a parser from the 'Parser' dropdown!")
//            return
//        }
//        if(params.targetObject == "null"){
//            render(status: 400, text: "Please select a target object from the 'Object' dropdown!")
//            return
//        }

        DynamicParser parser = DynamicParser.get(Integer.parseInt((String) params.parser))
		Set entries = new TreeSet(parser.entries.field.flatten())
		entries.addAll(parser.routines.procedures.created_entries.field.flatten())
        params.entries = entries.asList().sort()

        params.properties = grailsApplication.getArtefacts("Domain").find {
            it.fullName == params.targetObject
        }.persistantProperties
        params.properties = params.properties.name.sort()

        params.propertyCounter = 1

        render(template: "createTransformationRoutineSubElements", params: params)
    }

    def setProcedureProperties() {

        TransformationRoutine routine = TransformationRoutine.get(Integer.parseInt((String) params.belongs_to))

        if(params.crossMethod != "null") {
            params.method = params.crossMethod
            params.isCrossMethod = "true"
        }
        else{
            params.method = params.method
        }

        params.belongs_to = params.belongs_to

        if(MethodInfo.getSecondClassPropertiesPosition(params.method) != null) {
            def domainList = grailsApplication.getArtefacts("Domain")*.clazz
            // TODO only remove our parsing stuff, not everything from those packages. Test within another application using this plugin
            domainList.removeAll { it.name.split("\\.")[0].contains("extraction") || it.name.split("\\.")[0].contains("transformation") }
            params.domainList = []
            domainList.each { params.domainList.add(it.name) }
        }

        params.entries = getAllEntriesFromRoutine(routine)

        params.wrapperCount = MethodInfo.getWrapperCount(MethodInfo.fromString((String) params.method))

        for (int i = 0; i < (Integer) params.wrapperCount; i++)
            if (MethodInfo.isObjectWrapper((String) params.method, i))
                params.objectFields = grailsApplication.getArtefacts("Domain").find {
                    it.fullName == routine.target_object
                }.persistantProperties.name.sort()
//            else if (MethodInfo.isDatumWrapper((String) params["method"], i))
//                params.objectFields = params.entries

        if(params.method == "arithmeticOperation")
            params.entries.add(arithmeticPlaceholder)

        render(template: "createTransformationProcedureSubElements", params: params)
    }

    def addPropertyPair() {
        params.propertyCounter = Integer.parseInt((String) params.propertyCounter) + 1

        DynamicParser parser = DynamicParser.get(Integer.parseInt((String) params.belongs_to))
		Set entries = new TreeSet(parser.entries.field.flatten())
		entries.addAll(parser.routines.procedures.created_entries.field.flatten())
		params.entries = entries.asList().sort()

        params.properties = grailsApplication.getArtefacts("Domain").find {
            it.fullName == params.target_object
        }.persistantProperties
        params.properties = params.properties.name

        for (int i = 0; params["update_key" + i]; i++) {
            params["update_key" + i] = params["update_key" + i]
            params["update_value" + i] = params["update_value" + i]
        }

        render(template: "transformationRoutineProperties", params: params)
    }

    def setSecondClassProperties(){
        TransformationRoutine routine = TransformationRoutine.get(Integer.parseInt((String) params.belongs_to))

        params.entries = getAllEntriesFromRoutine(routine)
        params.method = params.method
        params.index = Integer.parseInt(params.index)
        params.secondPropertiesList = grailsApplication.getArtefacts("Domain").find{it.fullName == params.selectedClass}.persistantProperties.name.sort()

        for (int i = 0; i < MethodInfo.getWrapperCount(MethodInfo.fromString((String) params["method"])); i++)
            if (MethodInfo.isObjectWrapper((String) params["method"], i))
                params.objectFields = grailsApplication.getArtefacts("Domain").find {
                    it.fullName == routine.target_object
                }.persistantProperties.name.sort()

        render(template: "createTransformationProcedureSecondClassParameters", params: params)
    }

    def setArithmeticParameters(){
        TransformationRoutine routine = TransformationRoutine.get(Integer.parseInt((String) params.belongs_to))

        params.entries = getAllEntriesFromRoutine(routine)
        params.entries.add(arithmeticPlaceholder)

        params.right_value = params.right_value
        params.left_value = params.left_value

        params.arbitrary_right_value = params.arbitrary_right_value
        params.arbitrary_left_value = params.arbitrary_left_value

        if(params.right_value == arithmeticPlaceholder) {
            params.arbitrary_right_value = "true"
            params.right_value = ""
        }
        if(params.left_value == arithmeticPlaceholder) {
            params.arbitrary_left_value = "true"
            params.left_value = ""
        }

        render(template: "createTransformationProcedureArithmeticParameters", params: params)
    }

    def createTransformationRoutine() {
        DynamicParser parser = DynamicParser.get(Integer.parseInt((String) params["belongs_to"]))

        // If the user set no order id/sequence id, then we just iterate from 0 to "max int" until we find a free id
        if (params["order_id"] == "")
            if (parser.routines.order_id.size() != 0) {
                for (int i = 0; ; i++)
                    if (!parser.routines.order_id.contains(i)) {
                        params["order_id"] = i.toString()
                        break
                    }
            }
            else
                params["order_id"] = 0.toString()

        if (parser.routines.order_id.contains(Integer.parseInt((String) params["order_id"]))) {
            StringBuilder sequenceNumbersString = new StringBuilder()
            parser.routines.order_id.each {
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

        if (params["to_update"]) {
            for (int i = 0; params["update_key" + i] && params["update_key" + i] != "null" && params["update_value" + i] && params["update_value" + i] != "null"; i++) {

                // Problematic if field has a name which is predefined in grails. e.g. constraints, errors, log
                if (temp_class.declaredFields.collect().find { it.name == params["update_value" + i] } == null) {
                    render(status: 400, text: "The class '" + (String) params["target_object"] + "' does not have a property '" + (String) params["update_value" + i] + "'! Please go back and reconsider the update property parameter(s).")
                    return
                }

                if (parser.entries.field.find { it == params["update_key" + i] } == null) {
                    render(status: 400, text: "The parser '" + parser.name + "' does not have an entry '" + (String) params["update_key" + i] + "'! Please go back and reconsider the update entry parameter(s).")
                    return
                }

                tr.update_properties.put((String) params["update_key" + i], (String) params["update_value" + i])

                //params["update_key" + i] = (String) params["update_key" + i]
                //params["update_value" + i] = (String) params["update_value" + i]
            }
            if (tr.update_properties.size() == 0) {
                render(status: 400, text: "There are no update properties defined, even though the 'Update existing'-checkbox is checked!")
                return
            }

            //params["propertyCounter"] = i
        } else {
            while (params["update_key" + 0] && params["update_key" + 0] != "null" && params["update_value" + 0] && params["update_value" + 0] != "null") {
                render(status: 400, text: "Update properties were set even though 'Update existing'-checkbox was not set! Please rethink if you actually want to update existing objects.")
                return
            }
        }
        /*else{
            //params["propertyCounter"] = 0
        }*/

        tr.save(flush: true)
        if(tr.hasErrors()){
            // Took the generated error message and only output the first one without all the codes etc.
            // The split splits the error message in three parts, where we are interested in the second one because it contains the most relevant information
            render(status: 400, text: "Saving the routine produced an error: " + tr.errors.toString().split("\n|; codes")[1])
            return
        }

        parser.routines.add(tr)
        parser.save(flush: true)
        if(parser.hasErrors()){
            // Took the generated error message and only output the first one without all the codes etc.
            // The split splits the error message in three parts, where we are interested in the second one because it contains the most relevant information
            render(status: 400, text: "Saving the parser with the newly created routine produced an error: " + parser.errors.toString().split("\n|; codes")[1])
            return
        }

        render(status: 200)
    }

    def createTransformationProcedure() {
        TransformationRoutine routine = TransformationRoutine.get(Integer.parseInt((String) params["belongs_to"]))

        // If the user set no order id/sequence id, then we just iterate from 0 to "max int" until we find a free id
        if (params["order_id"] == "")
            if (routine.procedures.order_id.size() != 0) {
                for (int i = 0; ; i++)
                    if (!routine.procedures.order_id.contains(i)) {
                        params["order_id"] = i.toString()
                        break
                    }
        }
        else
            params["order_id"] = 0.toString()


        if (routine.procedures.order_id.contains(Integer.parseInt((String) params["order_id"]))) {
            StringBuilder sequenceNumbersString = new StringBuilder()
            routine.procedures.order_id.each {
                sequenceNumbersString.append(it)
                sequenceNumbersString.append(", ")
            }
            sequenceNumbersString.delete(sequenceNumbersString.length() - 2, sequenceNumbersString.length())

            render(status: 400, text: "The sequence numbers '" + sequenceNumbersString.toString() + "' are already taken! Please input a number unequal to the others.")
            return
        }

        TransformationProcedure tp = new TransformationProcedure()
        tp.transformation_method = TransformationService.&"$params.transformation_method"
        params.remove("transformation_method")
        tp.properties = params

        for(int i = 0; params["object_key" + i] && params["object_key" + i] != "null" && params["object_value" + i] && params["object_value" + i] != "null"; i++)
            tp.notable_objects.put((String) params["object_key" + i], (String) params["object_value" + i])

        // It makes no sense if we try to find a case which applies for this procedure and there is no criteria
        if((params["is_repetitive"] == "false" || params.is_repetitive == null) && tp.notable_objects.size() == 0){
            render(status: 400, text: "Procedure is marked as non-repetitive, but there is no notable object defined to be used as the search criteria!")
            return
        }

        ArrayList<ParamEntry> paramList

        // Cross-procedure stuff
        if(params.isCrossMethod == "true"){

            paramList = new ArrayList<ParamEntry>()
            // Boolean.parseBoolean() catches stuff like null and "true". using toString() then makes it pretty easy to evaluate the checkbox
            paramList.add(new ParamEntry(params.cross_method, Boolean.parseBoolean(params.cross_is_repetitive).toString()))

            for(int i = 0; params["cross_object_key" + i] && params["cross_object_key" + i] != "null" && params["cross_object_value" + i] && params["cross_object_value" + i] != "null"; i++)
                paramList.add(new ParamEntry((String) params["cross_object_key" + i], (String) params["cross_object_value" + i]))

            if(params.cross_is_repetitive == "false" && paramList.size() == 1){
                render(status: 400, text: "Cross-procedure is marked as non-repetitive, but there is no notable object defined to be used as the search criteria!")
                return
            }

            paramList.each{it.save(flush: true)}
            ParamEntryWrapper pw = new ParamEntryWrapper(paramList)
            pw.save(flush: true)
            if(pw.hasErrors()){
                StringBuilder wrapperEntryString = new StringBuilder()
                paramList.each{ wrapperEntryString.append(it.left_value + ": " + it.right_value + ";") }
                render(status: 400, text: "Saving the first pseudo-wrapper of the cross-procedure produced an error! Wrapper entries are: [${wrapperEntryString.toString()}]")
                return
            }

            tp.parameterWrappers.add(pw)
        }

        if(MethodInfo.isRightSideNotUsed(tp.transformation_method.method))
            params["parameter_value0_0"] = ""

        int j = 0
        boolean noParamsAdded = true
        for(int i = 0; (params["parameter_key" + i + "_" + j] && params["parameter_key" + i + "_" + j] != "null") || (params["parameter_value" + i + "_" + j] && params["parameter_value" + i + "_" + j] != "null"); i++){
            paramList = new ArrayList<ParamEntry>()
            for(j = 0; (params["parameter_key" + i + "_" + j] && params["parameter_key" + i + "_" + j] != "null") || (params["parameter_value" + i + "_" + j] && params["parameter_value" + i + "_" + j] != "null"); j++) {
                paramList.add(new ParamEntry((String)params["parameter_key" + i + "_" + j], (String)params["parameter_value" + i + "_" + j]))
                if(MethodInfo.isCreatedDatumWrapper(tp.transformation_method.method, i))
                    tp.created_entries.add(new DynamicParserEntry((String)params["parameter_key" + i + "_" + j], MethodInfo.getCreatedDatumDatatype(tp.transformation_method.method)))
            }

            paramList.each{it.save(flush: true)}
            ParamEntryWrapper pw = new ParamEntryWrapper(paramList)
            pw.save(flush: true)
            if(pw.hasErrors()){
                StringBuilder wrapperEntryString = new StringBuilder()
                paramList.each{ wrapperEntryString.append(it.left_value + ": " + it.right_value + ";") }
                render(status: 400, text: "Saving wrapper #${i} produced an error! Wrapper entries are: [${wrapperEntryString.toString()}]")
                return
            }

            tp.parameterWrappers.add(pw)
            noParamsAdded = false
            j = 0
        }

        if(noParamsAdded && MethodInfo.getWrapperCount((String)tp.transformation_method.method) != 0){
            render(status: 400, text: "No parameters were added to the procedure, please recheck!")
            return
        }

        // TODO expand all errors with additional info
        tp.save(flush: true)
        if(tp.hasErrors()){
            // Took the generated error message and only output the first one without all the codes etc.
            // The split splits the error message in three parts, where we are interested in the second one because it contains the most relevant information
            render(status: 400, text: "Saving the procedure produced an error: " + tp.errors.toString().split("\n|; codes")[1])
            return
        }

        try{
            TransformationService.checkIfParamsSetCorrectly(tp.transformation_method.method, tp)
        }
        catch(IncorrectSpecificationException e){
            render(status: 400, text: "Saving the procedure produced an error: " + e.toString())
            return
        }

        routine.procedures.add(tp)
        routine.save(flush: true)
        if(routine.hasErrors()){
            // Took the generated error message and only output the first one without all the codes etc.
            // The split splits the error message in three parts, where we are interested in the second one because it contains the most relevant information
            render(status: 400, text: "Saving the routine with the newly created procedure produced an error: " + routine.errors.toString().split("\n|; codes")[1])
            return
        }

        render(status: 200)
    }

    def getAllEntriesFromRoutine(TransformationRoutine routine){

        List<DynamicParser> parsers = DynamicParser.withCriteria { routines { idEq(routine.id) } }
        Set entries = new TreeSet(parsers.entries.field.flatten())
        entries.addAll(parsers.routines.flatten().findAll({ it.order_id <= routine.order_id}).procedures.created_entries.field.flatten())

        return entries.asList().sort()
    }

    /*def getAllEntriesFromBaseParser(DynamicParser parser){

        Set entries = new TreeSet(parser.entries.field.flatten())
        entries.addAll(parser.routines.procedures.created_entries.field.flatten())

        return entries.asList().sort()
    }*/
}
