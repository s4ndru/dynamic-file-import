package transformation

import extraction.*
import grails.util.Holders
import org.codehaus.groovy.runtime.MethodClosure
import testing.*

class TransformationService {

    // General purpose methods /////////////////////////////////////////////////////////////////////////////////////////

    static def transformAndLoadData(ArrayList<Map<String,Object>> data, DynamicParser parser){
        ArrayList<Object> temp

        parser.routines.each{ routine ->
            ArrayList<Object> objects = new ArrayList<Object>()
            data.each{objects.add(null)}

            for(int procedure_index = 0; procedure_index < routine.getProcedures().size(); procedure_index++) {
                TransformationProcedure procedure = routine.getProcedures().asList()[procedure_index]
                data.eachWithIndex { parse_object, object_index ->
                    boolean is_notable_line = false
                    procedure.notable_objects.each{if(parse_object[it.key] == parseToCorrespondingType(parse_object[it.key], it.value)){is_notable_line = true}}

                    if ((procedure.is_repetitive && !is_notable_line) || (!procedure.is_repetitive && is_notable_line)) {
                        Class target_class = getClassFromString(routine.target_object)
                        if (routine.to_update) {

                            List found_objects = target_class.where{
                                routine.update_properties.each{
                                    eq it.key, parse_object[it.value]
                                }
                            }.list()

                            if(found_objects.size() > 1)
                                // TODO: exception
                                println("exception!")

                            // TODO check what happens if null
                            objects[object_index] = found_objects[0]

                            //objects[object_index] = target_class.find(update_instance_criteria)

                            if (objects[object_index] == null)
                                objects[object_index] = target_class.newInstance()
                        }
                        else {
                            if (objects[object_index] == null)
                                objects[object_index] = target_class.newInstance()
                        }

                        temp = procedure.transformation_method(procedure, parse_object, objects[object_index])
                        parse_object = temp[0]
                        if (temp[1] != null)
                            objects[object_index] = temp[1]

                    }
                }

                // TODO: test more files with same parser/routine/procedure because of the removed temporary procedures
                if(procedure.temporary){
                    // Delete all wraps (and with them, their params) which are not used in other procedures.
                    def wrapsToDelete = new ArrayList<ParamEntryWrapper>();
                    procedure.parameterWrappers.each{ wrap ->
                        if((TransformationProcedure.findAll("from TransformationProcedure where ? in elements(parameterWrappers)", [wrap]).size()) == 1) {
                            def paramsToDelete = new ArrayList<ParamEntry>()
                            wrap.parameters.each{paramsToDelete.add(it)}
                            wrap.parameters.clear()
                            paramsToDelete.each{it.delete()}
                            wrapsToDelete.add(wrap)
                        }
                    }

                    wrapsToDelete.each{ wrap ->
                        procedure.parameterWrappers.remove(wrap)
                        wrap.delete()
                    }

                    routine.procedures.remove(procedure)

                    // TODO: philosophize why i am not allowed to flush the delete, without getting a hibernate exception
                    procedure.delete()
                    // TODO: philosophize about this. Could this be a problem?
                    procedure_index--
                }
            }
        }
        return
    }

    static def getClassFromString(String className) throws IncorrectSpecificationException{
        def grailsApplication = Holders.getGrailsApplication()
        Class object = grailsApplication.getDomainClass(className)?.clazz

        if(object == null)
            object = grailsApplication.domainClasses.find { it.clazz.simpleName == className }?.clazz

        if(object == null)
            throw new IncorrectSpecificationException("The specified classname-string does not match any domain classes")

        return object
    }

    static def parseToCorrespondingType(def obj, String fieldToParse){
        if(obj instanceof Float)
            try{
                return Float.parseFloat(fieldToParse)
            }
            catch(Exception e){
                throw new IncorrectSpecificationException("A specified entry is not the same type as it's corresponding field in the parsedentrymap")
            }
        else if(obj instanceof Integer)
            try{
                return Integer.parseInt(fieldToParse)
            }
            catch(Exception e)
            {
                throw new IncorrectSpecificationException("A specified entry is not the same type as it's corresponding field in the parsedentrymap")
            }
        else
            return fieldToParse
    }


    // Transformation methods //////////////////////////////////////////////////////////////////////////////////////////

    //// Simple Transformation methods /////////////////////////////////////////////////////////////////////////////////
    // TODO check everywhere if enough SortedSets and params are specified

    // Params:  1. Set: [propertyname in datum : value to append]
    static def appendStringLeftToField(TransformationProcedure procedure, Map<String, Object> datum, Object object_instance){
        def parameters = procedure.parameterWrappers.first().parameters.first()
        parameters.each{ parameter ->
            datum[parameter.left_value] = parameter.right_value + datum[parameter.left_value]
        }
        return [datum, null]
    }

    // Params:  1. Set: [propertyname in datum : value to append]
    static def appendStringRightToField(TransformationProcedure procedure, Map<String, Object> datum, Object object_instance){
        def parameters = procedure.parameterWrappers.first().parameters.first()
        parameters.each{ parameter ->
            datum[parameter.left_value] = datum[parameter.left_value] + parameter.right_value
        }
        return [datum, null]
    }

    // Params:  1. Set: [propertyname in datum (where to fetch the line to split) : token to split]
    //          2. Set: [propertyname in datum (where the result gets saved) : splitindex] => repeat X times
    // Note: if a splitindex is specified but cannot be found in the splitstring-result it is treated as "optional"
    static def splitStringField(TransformationProcedure procedure, Map<String, Object> datum, Object object_instance) {
        def param = procedure.parameterWrappers.first().parameters.asList().first()
        def mapping = procedure.parameterWrappers.asList()[1].parameters.asList()
        def line_to_split = datum[param.left_value]
        def split_lines = line_to_split.split("\\s*"+ param.right_value +"\\s*")

        mapping.each{
            if(datum.get(it.left_value) == null)
                datum.put(it.left_value, "")
            // TODO: throw exception if index not parseable to Int
            datum[it.left_value] = split_lines[Integer.parseInt(it.right_value)]
        }

        return [datum, null]
    }

    // Params:  1. Set: [propertyname in datum (where the result gets saved) : substring to put between each append (optional)]
    //          2. Set: [propertyname in datum (data to aggregate): propertyname in datum (data to aggregate)] => repeat X times
    static def appendFields(TransformationProcedure procedure, Map<String, Object> datum, Object object_instance) {
        // TODO
        return [datum, null]
    }

    // Params:  1. Set: [propertyname in datum (where the result gets saved) : multiplier for each value]
    //          2. Set: [propertyname in datum (data to aggregate) : propertyname in datum (data to aggregate)] => repeat X times
    static def calculateSum(TransformationProcedure procedure, Map<String, Object> datum, Object object_instance) {
        String multiplier = procedure.parameterWrappers.first().parameters.asList().first().right_value
        String property_identifier = procedure.parameterWrappers.first().parameters.asList().first().left_value
        def fields_to_aggregate = procedure.parameterWrappers.asList()[1].parameters.asList()
        def converted_fields = new ArrayList()
        // TODO think float through (floating point error)
        float result = 0

        fields_to_aggregate.each{
            if(datum[it.left_value] != null)
                converted_fields.add(datum[it.left_value])
            if(!it.right_value.equals("") && it.right_value != null && datum[it.right_value] != null)
                converted_fields.add(datum[it.right_value])
        }

        converted_fields.each{
            result += (it * ((multiplier == null || multiplier.equals("") || Float.parseFloat(multiplier) == 0) ? 1 : Float.parseFloat(multiplier)))
        }

        if(datum.get(property_identifier) == null)
            datum.put(property_identifier, "")
        datum[property_identifier] = result

        return [datum, null]
    }

    // Params:  1. Set: [propertyname in datum (where the result gets saved) : multiplier for each value]
    //          2. Set: [propertyname in datum (data to aggregate) : propertyname in datum (data to aggregate)] => repeat X times
    static def calculateMean(TransformationProcedure procedure, Map<String, Object> datum, Object object_instance) {
        String multiplier = procedure.parameterWrappers.first().parameters.asList().first().right_value
        String property_identifier = procedure.parameterWrappers.first().parameters.asList().first().left_value
        def fields_to_aggregate = procedure.parameterWrappers.asList()[1].parameters.asList()
        def converted_fields = new ArrayList()
        // TODO think float through (floating point error)
        float result = 0

        fields_to_aggregate.each{
            if(datum[it.left_value] != null)
                converted_fields.add(datum[it.left_value])
            if(!it.right_value.equals("") && it.right_value != null && datum[it.right_value] != null)
                converted_fields.add(datum[it.right_value])
        }

        float count = 0;
        converted_fields.each{
            result += (it * ((multiplier == null || multiplier.equals("") || Float.parseFloat(multiplier) == 0) ? 1 : Float.parseFloat(multiplier)))
            count++;
        }
        result /= count

        if(datum.get(property_identifier) == null)
            datum.put(property_identifier, "")
        datum[property_identifier] = result

        return [datum, null]
    }


    static def regexReplace(TransformationProcedure procedure, Map<String, Object> datum, Object object_instance) {
        // TODO
        return [datum, null]
    }

    // Params:  1. Set: [propertyname in datum (where we save the result) : classname]
    //          2. Set: [propertyname in class we are looking for : value for the property] => repeat X times
    static def findAndCacheRelation(TransformationProcedure procedure, Map<String, Object> datum, Object object_instance) {
        def params = procedure.parameterWrappers.asList()[0].parameters.asList()
        def search_params = procedure.parameterWrappers.asList()[1].parameters.asList()

        // TODO check if params[0].right_value is not null
        Class target_class = getClassFromString(params[0].right_value)
        /*Object instance = target_class.newInstance()    //eg TestResponsibility

        //build objectCriteria for searching object
        for(int i = 0; i < search_params.size(); i++)
            if(search_params[i].right_value != null && datum[search_params[i].right_value] != null)
                instance[search_params[i].left_value] = datum[search_params[i].right_value]
            else if(search_params[i].right_value != null)
                instance[search_params[i].left_value] = search_params[i].right_value
            else
                instance[search_params[i].left_value] = search_params[i].special_value*/

        List found_objects = target_class.where{
            search_params.each{
                if(it.right_value != null && datum[it.right_value] != null)
                    eq it.left_value, datum[it.right_value]
                else if(it.right_value != null)
                    eq it.left_value, it.right_value
                else
                    eq it.left_value, it.special_value
            }
        }.list()

        if(found_objects.size() > 1)
        // TODO: exception
            println("exception!")

        //find object with built criteria
        datum[params[0].left_value] = found_objects[0]

        return [datum, object_instance]
    }

    //// Complex transformation-methods ////////////////////////////////////////////////////////////////////////////////

    // Params:  1. Set: [propertyname in target class : Classname],
    //          2. Set: [propertyname in class we are looking for : fieldname in datum] => repeat X times
    static def createRelation(TransformationProcedure procedure, Map<String, Object> datum, Object object_instance) {
        def params = procedure.parameterWrappers.asList()[0].parameters.asList()
        def mapping = procedure.parameterWrappers.asList()[1].parameters.asList()

        Class target_class = getClassFromString(params.first().right_value)
        /*Object instance = target_class.newInstance()

        for(int i = 0; i < mapping.size(); i++)
            instance[mapping[i].left_value] = datum[mapping[i].right_value]*/

        List found_objects = target_class.where{
            mapping.each{
                eq it.left_value, datum[it.right_value]
            }
        }.list()

        if(found_objects.size() > 1)
        // TODO: exception
            println("exception!")

        object_instance[params.first().left_value] = found_objects[0]

        //print("ID: " + instance.objectNrInternal + ",product: " + instance.product)

//        object_instance[params.first().left_value] = target_class.find(instance)

        object_instance.save(flush: true)

        return [datum, object_instance]
    }


    // TODO: note => first set is always the name of the property in the target class. This is important for createNewProcedure
    // The new procedure will only be called last in the transformationroutine to prevent runtime inconsistencies.
    // E.g. it's not possible to call it early, if the corresponding data is not the first entry in a file.
    // Params:  1. Set: [transformationmethod-name : is repetitive("true"/"false")],
    //          and "notable objects": ["name of field", "value"] => repeat X times
    //          2. Set: [propertyname of a class : name of field in datum, in which the value is saved]
    // cont. => basically fishing parameters from the current datum, as a fixed value, for the next procedure => repeat X times
    // cont. #2 => this is the whole reason why we need the whole "createNewProcdure" sequence.
    //          3. Set (optional): [left value of parameter for procedure : right value of parameter for procedure]
    // cont. => just passing on parameters to the next procedure, which might be needed
    static def createNewTemporaryProcedure(TransformationProcedure procedure, Map<String, Object> datum, Object object_instance) {
        def params_procedure = procedure.parameterWrappers.first().parameters.asList()
        def runtime_params = procedure.parameterWrappers.asList()[1].parameters.asList()
        ParamEntryWrapper params_to_pass = null


        // Stuff for the new procedure
        SortedSet<ParamEntryWrapper> new_wraps = new TreeSet<ParamEntryWrapper>()
        def methodname = params_procedure[0].left_value
        MethodClosure method = TransformationService.&"$methodname"
        ArrayList<ParamEntry> params_for_new_wrap = new ArrayList<ParamEntry>()
        ParamEntryWrapper new_runtime_param_wrap
        Map<String, String> notable_objects = [:]

        for(int i = 1; i < params_procedure.size(); i++)
            notable_objects.put(params_procedure[i].left_value, params_procedure[i].right_value)

        if(procedure.parameterWrappers.size() == 3)
            params_to_pass = procedure.parameterWrappers.asList()[2]

        // TODO restrict order_id at creation of the procedure at the length of the procedure array of the routine
        int order_id = 0
        TransformationRoutine tr = TransformationRoutine.find("from TransformationRoutine where ? in elements(procedures)", [procedure])
        tr.procedures.each {
            if(it.order_id > order_id)
                order_id = it.order_id
        }

        runtime_params.each{
            params_for_new_wrap.add(new ParamEntry(it.left_value, datum[it.right_value]))
        }

        params_for_new_wrap.each{it.save(flush: true)}

        new_runtime_param_wrap = new ParamEntryWrapper(params_for_new_wrap)
        new_runtime_param_wrap.save(flush: true)

        // TODO: Note in Document how the order doesn't matter because the list gets sorted on its own anyways
        new_wraps.add(new_runtime_param_wrap)
        if(params_to_pass != null)
            new_wraps.add(params_to_pass)

        TransformationProcedure tp = new TransformationProcedure(order_id: order_id + 1, transformation_method: method,
                is_repetitive: Boolean.parseBoolean((params_procedure[0].right_value as String)), temporary: true, notable_objects: notable_objects)
        tp.parameterWrappers = new_wraps
        tp.save(flush: true)
        tr.procedures.add(tp)
        tr.save(flush: true)

        return [datum, object_instance]
    }

    // Params:  1. Set: [propertyname in target class : classname]
    //          2. Set: [propertyname in class we are looking for : value for the property] => repeat X times
    static def crossCreateRelation(TransformationProcedure procedure, Map<String, Object> datum, Object object_instance) {

        def params = procedure.parameterWrappers.asList()[0].parameters.asList()
        def search_params = procedure.parameterWrappers.asList()[1].parameters.asList()

        // TODO check if params[0].right_value is not null
        Class target_class = getClassFromString(params[0].right_value)
        Object instance = target_class.newInstance()    //eg TestResponsibility

        //build objectCriteria for searching object
        for(int i = 0; i < search_params.size(); i++)
            if(search_params[i].special_value != null)
                instance[search_params[i].left_value] = search_params[i].special_value   //eg special_value = **AM_NEU**
            else
                instance[search_params[i].left_value] = search_params[i].right_value

        //find object with built criteria
        object_instance[params[0].left_value] = target_class.find(instance)
        // eg params[0].left_value= responsibility
        //object_instance is object from class ColumnWidthTest so columWidthTest["responsibility"]=TestResponisbility.find(object with criteria name =  **AM_NEU**
        object_instance.save(flush: true)

        //datum ... Map of String of one tableLine
        //object_instance is the correlating object to one datum eg. of class ColumnWidthTest
        return [datum, object_instance]
    }

    // Params:  1. Set: [propertyname in target class : value for the property]
    static def crossAddValue(TransformationProcedure procedure, Map<String, Object> datum, Object object_instance) {
        def value_map = procedure.parameterWrappers.asList()[0].parameters.asList()

        // TODO: exception
        value_map.each{
            if(it.special_value != null)
                object_instance[it.left_value] = it.special_value
            else if(it.right_value != null)
                object_instance[it.left_value] = it.right_value
            else
                println("wtf!")
        }

        object_instance.save(flush: true)

        return [datum, object_instance]
    }

    // Loading methods ////////////////////////////////////////////////////////////////////////////////////////////////
    // Params:  1. Set: [propertyname of target class : propertyvalue] => repeat Entry 1 for each additional property
    static def identityTransfer(TransformationProcedure procedure, Map<String, Object> datum, Object object_instance) {
        def IO_map = procedure.parameterWrappers.first().parameters.asList()

        IO_map.each{ mapping ->

            if(object_instance.metaClass.getProperties().find{ class_it -> class_it.name == mapping.left_value} == null)
                //TODO throw exception
                println("Exception!")

            // so "optional" and "multiple" used in certain parser work.
            if(datum[mapping.right_value] != null)

            // so we can add "multiple" objects to a list instead of only setting entries
                if(object_instance.metaClass.getProperties().find{ class_it -> class_it.name == mapping.left_value}.type in Collection)
                    if(object_instance[mapping.left_value] == null)
                        object_instance[mapping.left_value] = [datum[mapping.right_value]]
                    else
                        object_instance[mapping.left_value].add(datum[mapping.right_value])
                else
                    object_instance[mapping.left_value] = datum[mapping.right_value]
        }

        object_instance.save(flush: true)

        return [datum, object_instance]

    }

    // TODO: exception if load fails (exception if there is an error after .save(flush: true))
    // TODO: if to_update is set, but object is not different from the one in the db => don't save


    static def saveAll(TransformationProcedure procedure, Map<String, Object> datum, Object object_instance) {

    }

    static def saveAllWhere(TransformationProcedure procedure, Map<String, Object> datum, Object object_instance) {

    }
}
