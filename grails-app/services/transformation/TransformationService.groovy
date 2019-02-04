package transformation

import extraction.*

import grails.util.Holders
import org.codehaus.groovy.runtime.MethodClosure

import java.sql.Timestamp
import java.util.regex.Matcher

class TransformationService {

    // Internally used methods /////////////////////////////////////////////////////////////////////////////////////////

    static def transformAndLoadData(ArrayList<Map<String,Object>> data, DynamicParser parser) throws Exception{
        // ArrayList<Object> temp

        parser.routines.each { routine ->
            ArrayList<Object> objects = new ArrayList<Object>()

            data.each { objects.add(null) }

            for (int procedure_index = 0; procedure_index < routine.getProcedures().size(); procedure_index++) {
                List<TransformationProcedure> procedures = routine.getProcedures().asList()
                TransformationProcedure procedure

                if(procedures.temporary.contains(true))
                    procedure = procedures[procedures.temporary.indexOf(true)]
                else
                    procedure = procedures[procedure_index]

                data.eachWithIndex { parse_object, object_index ->

                    boolean is_notable_line = false
                    procedure.notable_objects.each {
                        if (parse_object[it.key] == parseToCorrespondingType(parse_object[it.key], it.value)) {
                            is_notable_line = true
                        }
                    }

                    if ((procedure.is_repetitive && !is_notable_line) || (!procedure.is_repetitive && is_notable_line)) {
                        Class target_class = getClassFromString(routine.target_object)
                        StringBuilder propertiesErrorString = new StringBuilder()

                        if (routine.to_update) {
                            List found_objects = target_class.findAll {
                                routine.update_properties.each {
                                    propertiesErrorString.append(" " + it.value + "->" + parse_object[it.key] + ";")
                                    eq it.value, parse_object[it.key]
                                }
                            }

                            if (found_objects.size() > 1) {
                                StringBuilder updateCriteriaErrorString = new StringBuilder()
                                updateCriteriaErrorString.append("Problematic object is of type '${target_class.name}' with its compared properties being values:${propertiesErrorString.toString()}")

                                throw new ValidationException("Found multiple objects in database which apply for the 'update' criteria! " +
                                        "Update criteria (or a combination of it) must be unique to each object! " + updateCriteriaErrorString.toString())
                            }

                            objects[object_index] = found_objects[0]

                        }
                        if (objects[object_index] == null)
                            objects[object_index] = target_class.newInstance()

                        procedure.transformation_method(procedure, parse_object, objects[object_index])
/*                        temp = procedure.transformation_method(procedure, parse_object, objects[object_index])
                        if (temp[0] != null)
                            parse_object = temp[0]
                        if (temp[1] != null)
                            objects[object_index] = temp[1]*/

                    }
                }

                if (procedure.temporary) {
                    // Delete all wraps (and with them, their params) which are not used in other procedures.
                    def wrapsToDelete = new ArrayList<ParamEntryWrapper>()
                    procedure.parameterWrappers.each { wrap ->
                        if ((TransformationProcedure.findAll("from TransformationProcedure where ? in elements(parameterWrappers)", [wrap]).size()) == 1) {
                            def paramsToDelete = new ArrayList<ParamEntry>()
                            wrap.parameters.each { paramsToDelete.add(it) }
                            wrap.parameters.clear()
                            paramsToDelete.each { it.delete(flush: true) }
                            wrapsToDelete.add(wrap)
                        }
                    }

                    wrapsToDelete.each { wrap ->
                        procedure.parameterWrappers.remove(wrap)
                        wrap.delete(flush: true)
                    }

                    routine.procedures.remove(procedure)
                    routine.save(flush: true)

                    procedure.delete(flush: true)
                    procedure_index--
                }
            }
        }
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
                throw new IncorrectSpecificationException("Specified entry cannot be parsed to the data type of the object it is assigned to.")
            }
        else if(obj instanceof Integer)
            try{
                return Integer.parseInt(fieldToParse)
            }
            catch(Exception e){
                throw new IncorrectSpecificationException("Specified entry cannot be parsed to the data type of the object it is assigned to.")
            }
        else if(obj instanceof Long)
            try{
                return Long.parseLong(fieldToParse)
            }
            catch(Exception e){
                throw new IncorrectSpecificationException("Specified entry cannot be parsed to the data type of the object it is assigned to.")
            }
        else if(obj instanceof Boolean)
            try{
                return Boolean.parseBoolean(fieldToParse)
            }
            catch(Exception e){
                throw new IncorrectSpecificationException("Specified entry cannot be parsed to the data type of the object it is assigned to.")
            }
        else if(obj instanceof Double)
            try{
                return Double.parseDouble(fieldToParse)
            }
            catch(Exception e){
                throw new IncorrectSpecificationException("Specified entry cannot be parsed to the data type of the object it is assigned to.")
            }
        else if(obj instanceof String)
            try{
                return fieldToParse.toString()
            }
            catch(Exception e){
                throw new IncorrectSpecificationException("Specified entry cannot be parsed to the data type of the object it is assigned to.")
            }
        else
            return fieldToParse
    }

    // This helper function checks if there are as many wrappers defined as the function needs and if the wrappers
    // have at least one parameter entry or even multiple if the entry is repeatable as specified by the function
    static def checkIfParamsSetCorrectly(String methodName, TransformationProcedure procedure) throws IncorrectSpecificationException{
        // Check if a method has a correct number of wrappers set
        if(methodName != "cacheInfoForCrossProcedure" && procedure.parameterWrappers.size() != MethodInfo.getWrapperCount(methodName))
            throw new IncorrectSpecificationException("Found ${procedure.parameterWrappers.size()} instead of " +
                    "${MethodInfo.getWrapperCount(methodName)} parameter-wrappers in method: '${methodName}'")

        ArrayList<ParamEntryWrapper> wrappers = procedure.parameterWrappers.asList()
        for(int i = 0; i < procedure.parameterWrappers.size(); i++) {
            // Check if a method has at least one parametertuple per wrapper and not more than one if it is not a repeatable parameter tuple
            if (!MethodInfo.isRepeatable(methodName, i) && wrappers[i].parameters.size() > 1) {
                throw new IncorrectSpecificationException("Found a parameterlist of size bigger than 1 in wrapper #${i} " +
                        "of method: '${methodName}' in '${TransformationRoutine.find{procedures{id == 1}}}'" +
                        ", even though it is a non-repeatable parameter type!")
            }
            else if(wrappers[i].parameters.size() == 0) {
                throw new IncorrectSpecificationException("Found a parameter-list of size 0 in wrapper #${i} of method: '${methodName}' " +
                        "in '${TransformationRoutine.find{procedures{id == 1}}}'")
            }

            // Second parameter specified class is always in the first wrapper and on the right side
            if(MethodInfo.getSecondClassPropertiesPosition(methodName) != null && i == 0
                    && (wrappers[i].parameters.first().right_value == "null"
                    || wrappers[i].parameters.first().right_value == ""
                    || wrappers[i].parameters.first().right_value == null))
                throw new IncorrectSpecificationException("Found an empty classname the right value of wrapper #0 of method: '${methodName}' " +
                        "in '${TransformationRoutine.find{procedures{id == 1}}}'")
        }
    }


    // Transformation methods //////////////////////////////////////////////////////////////////////////////////////////

    //// Simple Transformation methods /////////////////////////////////////////////////////////////////////////////////

    // Params:  1. Set: [fieldname in parser created datum : value to append] => repeat X times
    static def appendString(TransformationProcedure procedure, Map<String, Object> datum, Object object_instance){
        checkIfParamsSetCorrectly("appendString", procedure)

        def parameters = procedure.parameterWrappers.first().parameters
        parameters.each{ parameter ->
            if(parameter.left_value != null && parameter.left_value != "" && parameter.left_value != "null" && datum[parameter.left_value] != null)
                datum[parameter.left_value] = datum[parameter.left_value] + parameter.right_value
        }
    }

    // Params:  1. Set: [value to prepend : fieldname parser created in datum] => repeat X times
    static def prependString(TransformationProcedure procedure, Map<String, Object> datum, Object object_instance){
        checkIfParamsSetCorrectly("prependString", procedure)

        def parameters = procedure.parameterWrappers.first().parameters
        parameters.each{ parameter ->
            if(parameter.right_value != null && parameter.right_value != "" && parameter.right_value != "null" && datum[parameter.right_value] != null)
                datum[parameter.right_value] = parameter.left_value + datum[parameter.right_value]
        }
    }

    // Params:  1. Set: [fieldname in datum : fieldname in datum] => repeat X times
    static def trimField(TransformationProcedure procedure, Map<String, Object> datum, Object object_instance){
        checkIfParamsSetCorrectly("trimField", procedure)

        def parameters = procedure.parameterWrappers.first().parameters
        parameters.each{ parameter ->
            if(parameter.left_value != null && parameter.left_value != "" && parameter.left_value != "null" && datum[parameter.left_value] != null)
                datum[parameter.left_value] = datum[parameter.left_value].trim()
            if(parameter.right_value != null && parameter.right_value != "" && parameter.right_value != "null" && datum[parameter.right_value] != null)
                datum[parameter.right_value] = datum[parameter.right_value].trim()
        }
    }


    // TODO Doc note => recommended to use this for array fields in simplePagParser
    // Params:  1. Set: [fieldname in parser created datum (where data is fetched) : token to split]
    //          2. Set: [fieldname in datum (where result will be saved) : splitindex] => repeat X times
    // Note: if a splitindex is specified but cannot be found in the splitstring-result it is treated as "optional"
    static def splitStringField(TransformationProcedure procedure, Map<String, Object> datum, Object object_instance) {
        checkIfParamsSetCorrectly("splitStringField", procedure)

        def param = procedure.parameterWrappers.first().parameters.first()
        def mapping = procedure.parameterWrappers.asList()[1].parameters
        def line_to_split = datum[param.left_value]
        def split_lines = line_to_split.split("\\s*"+ param.right_value +"\\s*")

        mapping.each{
            def index = Integer.parseInt(it.right_value)

            if(index < split_lines.size())
                datum[it.left_value] = split_lines[index]
        }
    }

    // Params:  1. Set: [fieldname in datum (where the result will be saved) : substring to put between each append (optional)]
    //          2. Set: [fieldname in datum (where data is fetched) : fieldname in datum (where data is fetched)] => repeat X times
    static def concatenateFields(TransformationProcedure procedure, Map<String, Object> datum, Object object_instance) {
        checkIfParamsSetCorrectly("concatenateFields", procedure)

        String fieldname = procedure.parameterWrappers.first().parameters.first().left_value
        String mid_string = procedure.parameterWrappers.first().parameters.first().right_value
        def fields_to_append = procedure.parameterWrappers.asList()[1].parameters

        StringBuilder result = new StringBuilder("")
        fields_to_append.eachWithIndex { it, index ->
            if (it.left_value != null && it.left_value != "" && it.left_value != "null" && datum[it.left_value] != null) {
                if (!result.toString().isEmpty())
                    result.append(mid_string)

                result.append(datum[it.left_value])
            }
            if (it.right_value != null && it.right_value != "" && it.right_value != "null" && datum[it.right_value] != null) {
                if (!result.toString().isEmpty())
                    result.append(mid_string)

                result.append(datum[it.right_value])
            }
        }

        datum[fieldname] = result.toString()
    }


    // Params:  1. Set: [fieldname in datum (where the result will be saved) : multiplier for each value]
    //          2. Set: [fieldname in datum (where data is fetched) : fieldname in datum (where data is fetched)] => repeat X times
    static def calculateSum(TransformationProcedure procedure, Map<String, Object> datum, Object object_instance) {
        checkIfParamsSetCorrectly("calculateSum", procedure)

        String multiplier = procedure.parameterWrappers.first().parameters.first().right_value
        String fieldName = procedure.parameterWrappers.first().parameters.first().left_value
        def fields_to_aggregate = procedure.parameterWrappers.asList()[1].parameters
        ArrayList<Float> converted_fields = new ArrayList()
        float result = 0

        fields_to_aggregate.each{
            if(it.left_value != "" && it.left_value != null && it.left_value != "null" && datum[it.left_value] != null)
                converted_fields.add(Float.parseFloat(datum[it.left_value].toString()))
            if(it.right_value != "" && it.right_value != null && it.right_value != "null" && datum[it.right_value] != null)
                converted_fields.add(Float.parseFloat(datum[it.right_value].toString()))
        }

        converted_fields.each{
            result += (it * ((multiplier == null || multiplier.equals("") || Float.parseFloat(multiplier) == 0) ? 1.0f : Float.parseFloat(multiplier)))
        }

        datum[fieldName] = result
    }

    // Params:  1. Set: [fieldname in datum (where the result will be saved) : multiplier for each value]
    //          2. Set: [fieldname in datum (where data is fetched) : fieldname in datum (where data is fetched)] => repeat X times
    static def calculateMean(TransformationProcedure procedure, Map<String, Object> datum, Object object_instance) {
        checkIfParamsSetCorrectly("calculateMean", procedure)

        String multiplier = procedure.parameterWrappers.first().parameters.first().right_value
        String fieldName = procedure.parameterWrappers.first().parameters.first().left_value
        def fields_to_aggregate = procedure.parameterWrappers.asList()[1].parameters
        ArrayList<Float> converted_fields = new ArrayList()
        float result = 0

        fields_to_aggregate.each{
            if(it.left_value != "" && it.left_value != null && it.left_value != "null" && datum[it.left_value] != null)
                converted_fields.add(Float.parseFloat(datum[it.left_value].toString()))
            if(it.right_value != "" && it.right_value != null && it.right_value != "null" && datum[it.right_value] != null)
                converted_fields.add(Float.parseFloat(datum[it.right_value].toString()))
        }

        float count = 0
        converted_fields.each{
            result += (it * ((multiplier == null || multiplier.equals("") || Float.parseFloat(multiplier) == 0) ? 1.0f : Float.parseFloat(multiplier)))
            count++
        }
        result /= count

        datum[fieldName] = result
    }

    // Params:  1. Set: [fieldname in datum (where the result will be saved) : identifier which operation (+, -, *, /, %)]
    //          2. Set: [fieldname in datum that is used as left operand (where data is fetched) : fieldname in datum that is used as right operand (where data is fetched)]
    static def arithmeticOperation(TransformationProcedure procedure, Map<String, Object> datum, Object object_instance) {
        checkIfParamsSetCorrectly("arithmeticOperation", procedure)

        String operator = procedure.parameterWrappers.first().parameters.first().right_value
        String fieldName = procedure.parameterWrappers.first().parameters.first().left_value
        def operands = procedure.parameterWrappers.asList()[1].parameters.first()

        Object left_operand
        Object right_operand

        if(datum[operands.left_value] != null)
            left_operand = datum[operands.left_value]
        else
            left_operand = Float.parseFloat(operands.left_value)

        if(datum[operands.right_value] != null)
            right_operand = datum[operands.right_value]
        else
            right_operand = Float.parseFloat(operands.right_value)

        if((datum[operands.left_value] != null && datum[operands.left_value] instanceof String) || (datum[operands.right_value] != null && datum[operands.right_value] instanceof String))
            throw new ValidationException("Found a 'String' object in the 'unaryOperation' transformation method. Unary arithmetic operations can only applied on numbers.")

        if(operator == "+")
            datum[fieldName] = left_operand + right_operand
        else if(operator == "-")
            datum[fieldName] = left_operand - right_operand
        else if(operator == "*")
            datum[fieldName] = left_operand * right_operand
        else if(operator == "/")
            datum[fieldName] = left_operand / right_operand
        else if(operator == "%")
            datum[fieldName] = left_operand % right_operand
    }

    // Params:  1. Set: [fieldname in datum : identifier which operation (++, --, -)] => Repeat X times
    static def unaryArithmeticOperation(TransformationProcedure procedure, Map<String, Object> datum, Object object_instance) {
        checkIfParamsSetCorrectly("unaryArithmeticOperation", procedure)

        def parameters = procedure.parameterWrappers.first().parameters

        parameters.eachWithIndex{ parameter, parameter_index ->
            if(datum[parameter.left_value] instanceof String)
                throw new ValidationException("Found a 'String' object in the 'unaryOperation' transformation method. Unary arithmetic operations can only applied on numbers.")

            if(parameter.right_value == "++" && datum[parameter.left_value] != null)
                datum[parameter.left_value]++
            else if(parameter.right_value == "--" && datum[parameter.left_value] != null)
                datum[parameter.left_value]--
            else if(parameter.right_value == "-" && datum[parameter.left_value] != null)
                datum[parameter.left_value] = -datum[parameter.left_value]
        }
    }

    // TODO Doc note => Can set something to "" (empty)
    // Params:  1. Set: [fieldname in datum (where the result will be saved) : fieldname in parser created datum (where data is fetched)]
    //          2. Set: [regex pattern : replacement value]
    static def regexReplace(TransformationProcedure procedure, Map<String, Object> datum, Object object_instance) {
        checkIfParamsSetCorrectly("regexReplace", procedure)

        String fieldName = procedure.parameterWrappers.first().parameters.first().left_value
        String fieldToFetch = procedure.parameterWrappers.first().parameters.first().right_value
        String regexPattern = procedure.parameterWrappers.asList()[1].parameters.first().left_value
        String replacement = procedure.parameterWrappers.asList()[1].parameters.first().right_value

        if(datum[fieldToFetch] != null)
            datum[fieldName] = datum[fieldToFetch].replaceAll(regexPattern, replacement)
    }

    // Params:  1. Set: [fieldname in datum (where the first non-empty field is saved) : Empty]
    //          2. Set: [fieldname in datum (where value is fetched) : fieldname in datum (where value is fetched)] => repeat X times
    static def setValueFromOptionalValues(TransformationProcedure procedure, Map<String, Object> datum, Object object_instance) {
        checkIfParamsSetCorrectly("setValueFromOptionalValues", procedure)

        def fieldName = procedure.parameterWrappers.first().parameters.first().left_value
        def fields = procedure.parameterWrappers.asList()[1].parameters

        // TODO Doc note => Converts to string, which is then later converted to the corresponding type of the property => see identityTransfer
        fields.each{
            if(datum[it.left_value] != "" && datum[it.left_value] != null && datum[it.left_value] != "null") {
                datum[fieldName] = datum[it.left_value]
                return
            }
            else if(datum[it.right_value] != "" && datum[it.right_value] != null && datum[it.right_value] != "null") {
                datum[fieldName] = datum[it.right_value]
                return
            }
        }
    }

    //// Complex transformation-methods ////////////////////////////////////////////////////////////////////////////////

    // TODO Doc Note => example for querying timestamp "select * from relation_test_class where timestamp >= '2018-11-05'"
    // Params:  1. Set: [propertyname in routine set target class : Empty]
    static def setTimestamp(TransformationProcedure procedure, Map<String, Object> datum, Object object_instance) {
        checkIfParamsSetCorrectly("setTimestamp", procedure)

        def params = procedure.parameterWrappers.first().parameters.first()

		if(object_instance.metaClass.getProperties().find{ class_it -> class_it.name == params.left_value} == null)
			throw new IncorrectSpecificationException("Can not find class with name '" + params.left_value + "'!")

        object_instance[params.left_value] = new Timestamp(new Date().getTime())
    }


    // TODO Doc note => Problem with finding something through float. Don't do this. Cast to Int or something
    // Params:  1. Set: [propertyname in routine set target class : Classname],
    //          2. Set: [propertyname in class (search query) : fieldname in parser created datum] => repeat X times
    static def createRelation(TransformationProcedure procedure, Map<String, Object> datum, Object object_instance) {
        checkIfParamsSetCorrectly("createRelation", procedure)

        def params = procedure.parameterWrappers.first().parameters.first()
        def mapping = procedure.parameterWrappers.asList()[1].parameters

        Class target_class = getClassFromString(params.right_value)
        StringBuilder propertiesErrorString = new StringBuilder()

        List found_objects = target_class.findAll{
            mapping.each{
                eq it.left_value, datum[it.right_value]
                propertiesErrorString.append(" " + it.left_value + "->" + datum[it.right_value] + ";")
            }
        }

        if(found_objects.size() > 1){
            StringBuilder updateCriteriaErrorString = new StringBuilder()
            updateCriteriaErrorString.append("Problematic object is of type '${target_class.name}' with its compared properties being values:${propertiesErrorString.toString()}")

            throw new ValidationException("Found multiple objects in database which apply for the 'update' criteria! " +
                    "Update criteria (or a combination of it) must be unique to each object! " + updateCriteriaErrorString.toString())
        }

		if(object_instance.metaClass.getProperties().find{ class_it -> class_it.name == params.left_value} == null)
			throw new IncorrectSpecificationException("Can not find class with name '" + params.left_value + "'!")

        object_instance[params.left_value] = found_objects[0]
    }

    // TODO Doc note => Problem with finding something through float. Don't do this. Cast to Int or something
    // Params:  1. Set: [propertyname in routine set target class : Classname],
    //          2. Set: [fieldname in parser created datum : propertyname in class (search query)] => repeat X times
    static def createOneToManyRelation(TransformationProcedure procedure, Map<String, Object> datum, Object object_instance) {
        checkIfParamsSetCorrectly("createOneToManyRelation", procedure)

        def params = procedure.parameterWrappers.first().parameters.first()
        def mapping = procedure.parameterWrappers.asList()[1].parameters

        Class target_class = getClassFromString(params.right_value)
        StringBuilder propertiesErrorString = new StringBuilder()

        List found_objects = target_class.findAll{
            mapping.each{
                eq it.left_value, datum[it.right_value]
                propertiesErrorString.append(" " + it.left_value + "->" + datum[it.right_value] + ";")
            }
        }

		if(object_instance.metaClass.getProperties().find{ class_it -> class_it.name == params.left_value} == null)
			throw new IncorrectSpecificationException("Can not find class with name '" + params.left_value + "'!")

        object_instance[params.left_value] = found_objects
    }


    // TODO: Doc note => first set is always the name of the property in the target class. This is important for createNewProcedure
    // TODO: Doc note => second set is optional. This means if there is no second set, then the third set becomes the second set
    // This is done to keep methods consistent with other methods and to have the last wrapper be the one where params are fetched from the data

    // The new procedure will only be called last in the transformationroutine in an attempt to prevent runtime inconsistencies.
    // E.g. it's not possible to call it early, if the corresponding data is not the first entry in a file.
    // Params:  1. Set: [transformationmethod-name : is repetitive("true"/"false")],
    //          and "notable objects": ["name of field", "value"] => repeat X times
    //          2. Set (optional): [left value of parameter for procedure : right value of parameter for procedure]
    //              cont. => just passing on parameters to the next procedure, which might be needed => repeat X times
    //          3. Set: [propertyname of routine set class : name of field in datum, in which the value is saved]
    //              cont. => basically fishing parameters from the current datum, as a fixed value, for the next procedure => repeat X times
    //              cont. #2 => this is the whole reason why we need the whole "createNewProcedure" sequence.

    static def cacheInfoForCrossProcedure(TransformationProcedure procedure, Map<String, Object> datum, Object object_instance) {
        checkIfParamsSetCorrectly("cacheInfoForCrossProcedure", procedure)

        def params_procedure = procedure.parameterWrappers.first().parameters.asList()

        // Stuff for the new procedure
        // First left_value is the methodname of the cross-method
        def methodname = params_procedure[0].left_value
        MethodClosure method = TransformationService.&"$methodname"

        def runtime_params = procedure.parameterWrappers.asList()[MethodInfo.getWrapperCount(methodname)].parameters.asList()
        SortedSet<ParamEntryWrapper> new_wraps = new TreeSet<ParamEntryWrapper>()
        ArrayList<ParamEntry> params_for_new_wrap = new ArrayList<ParamEntry>()
        ParamEntryWrapper new_runtime_param_wrap
        Map<String, String> notable_objects = [:]

		// TODO: Doc note => No problem with order_id because the temp procedure is always executed right after it was created
		TransformationRoutine tr = TransformationRoutine.find("from TransformationRoutine where ? in elements(procedures)", [procedure])

		if(tr.procedures.temporary.contains(true))
			throw new ValidationException("'cacheInfoForCrossProcedure' is trying to create another temporary procedure even though such already exists for this routine. This means the specified data-set is not unique!")

        for(int i = 1; i < params_procedure.size(); i++)
            notable_objects.put(params_procedure[i].left_value, params_procedure[i].right_value)

        runtime_params.each{
            params_for_new_wrap.add(new ParamEntry(it.left_value, datum[it.right_value].toString()))
        }

        params_for_new_wrap.each{it.save(flush: true)}

        new_runtime_param_wrap = new ParamEntryWrapper(params_for_new_wrap)
        new_runtime_param_wrap.save(flush: true)

        new_wraps.add(new_runtime_param_wrap)
        if(procedure.parameterWrappers.size() == 3)
            new_wraps.add(procedure.parameterWrappers.asList()[1])

        TransformationProcedure tp = new TransformationProcedure(order_id: -1, transformation_method: method,
                is_repetitive: Boolean.parseBoolean((params_procedure[0].right_value as String)), temporary: true, notable_objects: notable_objects)
        tp.parameterWrappers = new_wraps
        tp.save(flush: true)
        tr.procedures.add(tp)
        tr.save(flush: true)

		if(tr.hasErrors()) {
			StringBuilder errorString = new StringBuilder()
			tr.errors.allErrors.each { errorString.append(it.toString() + ";") }
			throw new DatabaseSaveException("There was an error while loading the data into the database. Maybe some constraints are not fulfilled?\n" + errorString.toString())
		}
    }

    // Params:  1. Set: [propertyname in object which is set as the foreign key (defined by routine) : classname]
    //          2. Set: [propertyname in object which is used for the search : value for the property (set by 'cacheInfoForCrossProcedure')] => repeat X times
    // So basically, the instance which is found in the database with the properties specified by the 2. set,
    // is then saved in the property specified by the 1. set
    static def crossCreateRelation(TransformationProcedure procedure, Map<String, Object> datum, Object object_instance) {
        checkIfParamsSetCorrectly("crossCreateRelation", procedure)

        def params = procedure.parameterWrappers.first().parameters.first()
        def search_params = procedure.parameterWrappers.asList()[1].parameters

        Class target_class = getClassFromString(params.right_value)

        // Is used for the method "parseToCorrespondingType"
        Object instance = target_class.newInstance()    //eg TestResponsibility

        StringBuilder propertiesErrorString = new StringBuilder()

        //build objectCriteria for searching object
        List found_objects = target_class.findAll {
            search_params.each {
                if (it.right_value != null && it.right_value != "" && it.right_value != "null") {
                    eq it.left_value, parseToCorrespondingType(instance[it.left_value], it.right_value)
                    propertiesErrorString.append(" " + it.left_value + "->" + it.right_value + ";")
                }
                else
                    new ValidationException("Right value of parameter of method 'crossCreateRelation' has no values set!")
            }
        }

//        for(int i = 0; i < search_params.size(); i++)
//            if(search_params[i].special_value != null)
//                instance[search_params[i].left_value] = search_params[i].special_value   //eg special_value = **AM_NEU**
//            else if(search_params[i].right_value != null)
//                instance[search_params[i].left_value] = search_params[i].right_value
//            else
//                new ValidationException("Right value of parameter of method 'crossCreateRelation' has no values set!")

        // TODO: Doc note => if nothing is found, it's set to null
        //find object with built criteria
        //object_instance[params[0].left_value] = target_class.find(instance)

        if (found_objects.size() > 1) {
            StringBuilder updateCriteriaErrorString = new StringBuilder()
            updateCriteriaErrorString.append("Problematic object is of type '${target_class.name}' with its compared properties being values:${propertiesErrorString.toString()}")

            throw new ValidationException("Found multiple objects in database which apply for the 'search' criteria! " +
                    "Update criteria (or a combination of it) must be unique to each object! " + updateCriteriaErrorString.toString())
        }

        if(object_instance.metaClass.getProperties().find{ class_it -> class_it.name == params.left_value} == null)
            throw new IncorrectSpecificationException("Can not find class with name '" + params.left_value + "'!")

        object_instance[params.left_value] = found_objects[0]
    }

    // Params:  1. Set: [propertyname in routine set target class : value for property] => repeat X times
    static def crossSetValue(TransformationProcedure procedure, Map<String, Object> datum, Object object_instance) {
        checkIfParamsSetCorrectly("crossSetValue", procedure)

        def value_map = procedure.parameterWrappers.first().parameters

        value_map.each{
			if(it.right_value != null && it.right_value != "" && it.right_value != "null"){
				if(object_instance.metaClass.getProperties().find{ class_it -> class_it.name == it.left_value} == null)
					throw new IncorrectSpecificationException("Can not find class with name '" + it.left_value + "'!")

				object_instance[it.left_value] = parseToCorrespondingType(object_instance[it.left_value], it.right_value)
			}
            else
                new ValidationException("Right value of parameter of method 'crossAddValue' has no values set!")
        }
    }

    // Loading methods ////////////////////////////////////////////////////////////////////////////////////////////////
    // Params:  1. Set: [propertyname in routine set target class : fieldname in parser created datum] => repeat X times
    static def identityTransfer(TransformationProcedure procedure, Map<String, Object> datum, Object object_instance) {
        checkIfParamsSetCorrectly("identityTransfer", procedure)

        def IO_map = procedure.parameterWrappers.first().parameters

        IO_map.each{ mapping ->
            if(object_instance.metaClass.getProperties().find{ class_it -> class_it.name == mapping.left_value} == null)
                throw new IncorrectSpecificationException("Can not find class with name '" + mapping.left_value + "'!")

            if(datum[mapping.right_value] != null)
                object_instance[mapping.left_value] = parseToCorrespondingType(object_instance[mapping.left_value], datum[mapping.right_value].toString())
        }
    }

    // Params:  1. Set: [propertyname in routine set target class : fieldname in parser created datum] => repeat X times
    static def identityTransferAndSave(TransformationProcedure procedure, Map<String, Object> datum, Object object_instance) {
        checkIfParamsSetCorrectly("identityTransferAndSave", procedure)

        def IO_map = procedure.parameterWrappers.first().parameters

        IO_map.each{ mapping ->
            if(object_instance.metaClass.getProperties().find{ class_it -> class_it.name == mapping.left_value} == null)
                throw new IncorrectSpecificationException("Can not find class with name '" + mapping.left_value + "'!")

            if(datum[mapping.right_value] != null)
                object_instance[mapping.left_value] = parseToCorrespondingType(object_instance[mapping.left_value], datum[mapping.right_value].toString())
        }

        object_instance.save(flush: true)

        if(object_instance.hasErrors()) {
            StringBuilder errorString = new StringBuilder()
            object_instance.errors.allErrors.each { errorString.append(it.toString() + ";") }
            throw new DatabaseSaveException("There was an error while loading the data into the database. Maybe some constraints are not fulfilled?\n" + errorString.toString())
        }
    }

    // Params:  No Params
    static def saveAllObjects(TransformationProcedure procedure, Map<String, Object> datum, Object object_instance) {
        checkIfParamsSetCorrectly("saveAllObjects", procedure)

        object_instance.save(flush: true)

        if(object_instance.hasErrors()) {
            StringBuilder errorString = new StringBuilder()
            object_instance.errors.allErrors.each { errorString.append(it.toString() + ";") }
            throw new DatabaseSaveException("There was an error while loading the data into the database. Maybe some constraints are not fulfilled?\n" + errorString.toString())
        }
    }

    // TODO Doc note => no conditional saves because we have other tools to filter entries and thus objects (i.e. notable objects)
}
