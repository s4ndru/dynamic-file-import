package transformation

import extraction.EntryDataType

/**
 * Created by Sandr on 04.03.2017.
 */

enum MethodInfo {
    APPENDSTRING("appendString"),
    PREPENDSTRING("prependString"),
    SPLITSTRINGFIELD("splitStringField"),
    CONCATENATEFIELDS("concatenateFields"),
    CALCULATESUM("calculateSum"),
    CALCULATEMEAN("calculateMean"),
	ARITHMETICOPERATION("arithmeticOperation"),
	UNARYARITHMETICOPERATION("unaryArithmeticOperation"),
    SETTIMESTAMP("setTimestamp"),
    REGEXREPLACE("regexReplace"),
    SETVALUEFROMOPTIONALVALUES("setValueFromOptionalValues"),
    CREATERELATION("createRelation"),
    CREATEONETOMANYRELATION("createOneToManyRelation"),
    CACHEINFOFORCROSSPROCEDURE("cacheInfoForCrossProcedure"),
    CROSSCREATERELATION("crossCreateRelation"),
    CROSSSETVALUE("crossSetValue"),
    IDENTITYTRANSFER("identityTransfer"),
    IDENTITYTRANSFERANDSAVE("identityTransferAndSave"),
    SAVEALLOBJECTS("saveAllObjects")

    String propertyName

    // How many wrappers does each transformation-method have
    static LinkedHashMap<String, Integer> wrapperCountMap =
            ["appendString": 1,
             "prependString": 1,
             "trimField": 1,
             "splitStringField": 2,
             "concatenateFields": 2,
             "calculateSum": 2,
             "calculateMean": 2,
			 "arithmeticOperation": 2,
			 "unaryArithmeticOperation": 1,
             "regexReplace": 2,
             "setValueFromOptionalValues": 2,
//             "findAndCacheRelation": 2,
             "setTimestamp": 1,
             "createRelation": 2,
             "createOneToManyRelation": 2,
             "cacheInfoForCrossProcedure": 3,
             "crossCreateRelation": 2,
             "crossSetValue": 1,
             "identityTransfer": 1,
             "identityTransferAndSave": 1,
             "saveAllObjects": 0]

    // Wrapper index of the data sets which are set by the parsers for each transformation method
    // E.g.: A "0" in datumWrapperPositionMap tells us that the first wrapper contains the parser created data
    static LinkedHashMap<String, Integer> datumWrapperPositionMap =
            ["appendString": 0,
             "prependString": 0,
             "splitStringField": 0,
             "concatenateFields": 1,
             "calculateSum": 1,
             "calculateMean": 1,
			 "arithmeticOperation": 1,
			 "unaryArithmeticOperation": 0,
             "regexReplace": 0,
             "setValueFromOptionalValues": 1,
//             "findAndCacheRelation": 0,
             "createRelation": 1,
             "createOneToManyRelation": 1,
             "crossCreateRelation": 1,
             "crossSetValue": 0,
             "identityTransfer": 0,
             "identityTransferAndSave": 0]

    // Which wrappers of specific transformation-methods have partial or full set utilization
    // To be used in combination with datumWrapperPositionMap
    // E.g.: [0,1] means the "left" and "right side" of the tuple is in use for the parser created data
    static LinkedHashMap<String, List<Integer>> datumTuplePositionMap =
            ["appendString": [0],
             "prependString": [1],
             "trimField": [0,1],
             "splitStringField": [0],
             "concatenateFields": [0,1],
             "calculateSum": [0,1],
             "calculateMean": [0,1],
			 "arithmeticOperation": [0,1],
			 "unaryArithmeticOperation": [0],
             "regexReplace": [1],
             "setValueFromOptionalValues": [0,1],
//             "findAndCacheRelation": [0],
             "createRelation": [1],
             "createOneToManyRelation": [1],
             "crossCreateRelation": [1],
             "crossSetValue": [1],
             "identityTransfer": [0],
             "identityTransferAndSave": [0]]


    // Which wrappers of transformation methods are working with the routine defined object properties
    static LinkedHashMap<String, Integer> objectWrapperPositionMap =
            ["setTimestamp": 0,
             "createRelation": 0,
             "createOneToManyRelation": 0,
             "crossCreateRelation": 0,
             "crossSetValue": 0,
             "identityTransfer": 0,
             "identityTransferAndSave": 0]

    // Which side of the tuple (left/right) is using the routine defined object properties
    static LinkedHashMap<String, List<Integer>> objectTuplePositionMap =
            ["setTimestamp": [0],
             "createRelation": [0],
             "createOneToManyRelation": [0],
             "crossCreateRelation": [0],
             "crossSetValue": [0],
             "identityTransfer": [1],
             "identityTransferAndSave": [1]]

    // Position of procedure created data in wrappers
    static LinkedHashMap<String, Integer> createdDatumWrapperPositionMap =
            ["splitStringField": 1,
             "concatenateFields": 0,
             "calculateSum": 0,
             "calculateMean": 0,
             "arithmeticOperation": 0,
             "regexReplace": 0,
             "setValueFromOptionalValues": 0]

    // The datatype of the entries that some methods are creating
    static LinkedHashMap<String, EntryDataType> createdDatumDatatypeMap =
            ["splitStringField": EntryDataType.STRING,
             "concatenateFields": EntryDataType.STRING,
             "calculateSum": EntryDataType.FLOAT,
             "calculateMean": EntryDataType.FLOAT,
             "arithmeticOperation": EntryDataType.FLOAT,
             "regexReplace": EntryDataType.STRING,
             "setValueFromOptionalValues": EntryDataType.STRING]

//    // Position of procedure created data in tuples
//    static LinkedHashMap<String, Integer> methodCreatedDatumTuplePositionMap =
//            ["splitStringField": 1,
//             "calculateSum": 0,
//             "calculateMean": 0]

    // Which wrappers of methods that allow for repeatable entries
    static LinkedHashMap<String, List<Integer>> repeatableSetsMap =
             ["appendString": [0],
              "prependString": [0],
              "splitStringField": [1],
              "concatenateFields": [0,1],
              "calculateSum": [1],
              "calculateMean": [1],
			  "unaryArithmeticOperation": [0],
              "setValueFromOptionalValues": [1],
//              "findAndCacheRelation": [1],
              "createRelation": [1],
              "createOneToManyRelation": [1],
              "cacheInfoForCrossProcedure": [0,1,2],
              "crossCreateRelation": [1],
              "crossSetValue": [0],
              "identityTransfer": [0],
              "identityTransferAndSave": [0]]

    // Which methods are using a second class and thus the position of the class properties in the following wrapper
    static LinkedHashMap<String, Integer> secondClassPropertiesPositionMap =
//            ["findAndCacheRelation": 0,
             ["createRelation": 0,
              "createOneToManyRelation": 0,
              "crossCreateRelation": 0]

    // Which methods can be used in conjunction with "cacheInfoForCrossProcedure"
    static List<String> crossProceduresMap =
            ["crossCreateRelation",
             "crossSetValue"]

    // Which methods do not use the map fully (right side of the first wrapper is empty)
    static List<String> rightSideUnusedMap =
            ["setTimestamp",
             "setValueFromOptionalValues"]

    static List<String> supportedArithmeticOperators =
            ["+",
             "-",
             "*",
             "/",
             "%"]

    static List<String> supportedUnaryArithmeticOperators =
            ["++",
             "--",
             "-"]

    MethodInfo(String propertyName) {
        this.propertyName = propertyName
    }

    String toString(){
        return propertyName
    }

    static MethodInfo fromString(String text) {
        if (text != null) {
            for (MethodInfo type : values()) {
                if (text.equalsIgnoreCase(type.propertyName)) {
                    return type
                }
            }
        }
        return null
    }

    static Integer getWrapperCount(MethodInfo m){
        return wrapperCountMap.get(m.toString())
    }

    static Integer getWrapperCount(String m){
        return wrapperCountMap.get(m)
    }

    // Checks if wrapper on an index is the wrapper which contains the data
    static Boolean isDatumWrapper(String m, Integer index){
        return datumWrapperPositionMap.get(m) != null && datumWrapperPositionMap.get(m) == index
    }

    // Returns a list which contains information on which side in the tuple is used for data
    // (or if both are used or not at all)
    static Boolean getDatumTuplePosition(String m, Integer position){
        return datumTuplePositionMap.get(m) != null && datumTuplePositionMap.get(m).contains(position)
    }

    static Boolean isDatumOnWrapperAndTuplePosition(String m, Integer wrapperIndex, Integer position){
        return isDatumWrapper(m, wrapperIndex) && getDatumTuplePosition(m, position)
    }

    // Checks if wrapper on an index is the wrapper which works with the routine defined object
    static Boolean isObjectWrapper(String m, Integer index){
        return objectWrapperPositionMap.get(m) != null && objectWrapperPositionMap.get(m) == index
    }

    static Boolean getObjectTuplePosition(String m, Integer position){
        return objectTuplePositionMap.get(m) != null && objectTuplePositionMap.get(m).contains(position)
    }

    static Boolean isObjectOnWrapperAndTuplePosition(String m, Integer wrapperIndex, Integer position){
        return isObjectWrapper(m, wrapperIndex) && getObjectTuplePosition(m, position)
    }

    // Self-explanatory
    static Boolean isRepeatable(String m, Integer index){
        return repeatableSetsMap.get(m) != null && repeatableSetsMap.get(m).contains(index)
    }

    static Boolean isCreatedDatumWrapper(String m, Integer index){
        return createdDatumWrapperPositionMap.get(m) != null && createdDatumWrapperPositionMap.get(m) == index
    }

    // Returns the position of the properties of a class which is specified in the method parameters
    // This function can be used to figure out if a method is even using a parameter specified second class
    static Integer getSecondClassPropertiesPosition(String m){
        return secondClassPropertiesPositionMap.get(m)
    }

    static EntryDataType getCreatedDatumDatatype(String m){
        return createdDatumDatatypeMap.get(m)
    }

    static List<String> getAllProceduresExceptCrossProcedures(){
        ArrayList<String> procedureNames = values().propertyName
        crossProceduresMap.each{procedureNames.remove(it)}
        return procedureNames
    }

    static List<String> getCrossProceduresMap(){
        return crossProceduresMap
    }

    static Boolean isRightSideNotUsed(String m){
        return rightSideUnusedMap.contains(m)
    }

    static List<String> getArithmeticOperators(){
        return supportedArithmeticOperators
    }

    static List<String> getUnaryArithmeticOperators(){
        return supportedUnaryArithmeticOperators
    }
}
