package transformation

import java.util.HashMap;

/**
 * Created by Sandr on 04.03.2017.
 */

// TODO don't forget to add the new transformation methods
enum MethodInfo {
    APPENDSTRING("appendString"),
    PREPENDSTRING("prependString"),
    SPLITSTRINGFIELD("splitStringField"),
    APPENDFIELDS("appendFields"),
    CALCULATESUM("calculateSum"),
    CALCULATEMEAN("calculateMean"),
    REGEXREPLACE("regexReplace"),
    FINDANDCACHERELATION("findAndCacheRelation"),
    CREATERELATION("createRelation"),
    CREATENEWTEMPORARYPROCEDURE("createNewTemporaryProcedure"),
    CROSSCREATERELATION("crossCreateRelation"),
    CRESSADDVALUE("crossAddValue"),
    IDENTITYTRANSFER("identityTransfer"),
    SAVEALL("saveAll"),
    SAVEALLWHERE("saveAllWhere")

    // How many wrappers does each transformation-method have
    static LinkedHashMap<String, Integer> methodWrapperCountMap =
            ["appendString": 1,
             "prependString": 1,
             "trimField": 1,
             "splitStringField": 2,
             "appendFields": 2,
             "calculateSum": 2,
             "calculateMean": 2,
             "findAndCacheRelation": 2,
             "createRelation": 2,
             "createNewTemporaryProcedure": 3,
             "crossCreateRelation": 2,
             "crossAddValue": 1,
             "identityTransfer": 1,
             "saveAll": 1,
             "saveAllWhere": 1]

    // Wrapper index of the data sets which are set by the parsers for each transformation method
    // E.g.: A "0" in methodDataMap tells us that the first wrapper contains the relevant datatuples
    static LinkedHashMap<String, Integer> methodDataMap =
            ["appendString": 0,
             "prependString": 0,
             "splitStringField": 0,
             "appendFields": 1,
             "calculateSum": 1,
             "calculateMean": 1,
             "findAndCacheRelation": 0,
             "createRelation": 1,
             "identityTransfer": 0]

    // Which wrappers of specific transformation-methods have partial or full set utilization
    // To be used in combination with methodDataMap
    // E.g.: [0,1] means the "left" and "right side" of the datatuple is in use
    static LinkedHashMap<String, List<Integer>> methodSetAccessMap =
            ["appendFields": [0,1],
             "calculateSum": [0,1],
             "calculateMean": [0,1],
             "createRelation": [1],
             "identityTransfer": [1]]

    // Which wrappers of transformationmethods are working with the routine defined object
    static LinkedHashMap<String, Integer> methodObjectMappingMap =
            ["createRelation": 1,
             "identityTransfer": 0]

    // Which wrappers of transformationmethods allow for repeatable entries
    static LinkedHashMap<String, List<Integer>> methodReapeatableSetsMap =
             ["appendString": [0],
              "prependString": [0],
              "splitStringField": [1],
              "appendFields": [1],
              "calculateSum": [1],
              "calculateMean": [1],
              "findAndCacheRelation": [1],
              "createRelation": [1],
              "identityTransfer": [0]]

    String propertyName

    MethodInfo(String propertyName) {
        this.propertyName = propertyName
    }

    String toString(){
        return propertyName
    }


    // Self-explanatory
    static Integer getWrapperCount(MethodInfo m){
        return methodWrapperCountMap.get(m.toString())
    }

    // Checks if wrapper on an index is the wrapper which contains the data
    static Boolean isDataWrapper(String m, Integer index){
        Boolean is_datum = false
        is_datum = methodDataMap.get(m) != null && methodDataMap.get(m) == index
        return is_datum
    }

    // Returns a list which contains information on which side in the tuple is used for data
    // (or if both are used or not at all)
    static List<Integer> getDatumSetPosition(String m){
        return methodSetAccessMap.get(m)
    }

    // Checks if wrapper on an index is the wrapper which works with the routine defined object
    static Boolean isFromObject(String m, Integer index){
        return methodObjectMappingMap.get(m) != null && methodObjectMappingMap.get(m) == index
    }

    // Self-explanatory
    static Boolean isRepeatable(String m, Integer index){
        return methodReapeatableSetsMap.get(m) != null && methodReapeatableSetsMap.get(m).contains(index)
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
}
