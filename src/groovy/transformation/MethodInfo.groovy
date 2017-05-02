package transformation

import java.util.HashMap;

/**
 * Created by Sandr on 04.03.2017.
 */
enum MethodInfo {
    APPENDSTRINGLEFTTOFIELD("appendStringLeftToField"),
    APPENDSTRINGRIGHTTOFIELD("appendStringRightToField"),
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

    // How many wrappers does each transformationmethod have
    static LinkedHashMap<String, Integer> MethodWrapperCountMap =
            ["appendStringLeftToField": 1,
             "appendStringRightToField": 1,
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

    // Which sets of specific transformationmethods are working with the parserset data
    static LinkedHashMap<String, Integer> MethodDataMap =
            ["appendStringLeftToField": 0,
             "appendStringRightToField": 0,
             "splitStringField": 0,
             "appendFields": 1,
             "calculateSum": 1,
             "calculateMean": 1,
             "findAndCacheRelation": 0,
             "createRelation": 1,
             "identityTransfer": 0]

    // Which sets of specific transformationmethods have unusual or full set utilization
    // To be used in combination with MethodDataMap
    static LinkedHashMap<String, List<Integer>> methodSetAccessMap =
            ["appendFields": [0,1],
             "calculateSum": [0,1],
             "calculateMean": [0,1],
             "createRelation": [1],
             "identityTransfer": [1]]

    // Which sets of specific transformationmethods are working with the routine defined object
    static LinkedHashMap<String, Integer> methodObjectMappingMap =
            ["createRelation": 1,
             "identityTransfer": 0]

    // Which sets of specific transformationmethods allow for multiple entries
    static LinkedHashMap<String, List<Integer>> methodReapeatableSetsMap =
             ["appendStringLeftToField": [0],
              "appendStringRightToField": [0],
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

    static Integer getWrapperCount(MethodInfo m){
        return MethodWrapperCountMap.get(m.toString())
    }

    static Boolean isDatum(String m, Integer index){
        Boolean is_datum = false

        is_datum = MethodDataMap.get(m) == index

        return is_datum
    }
    static List<Integer> getDatumSetPosition(String m){
        return methodSetAccessMap.get(m)
    }

    static Boolean isFromObject(String m, Integer index){
        return methodObjectMappingMap.get(m) != null && methodObjectMappingMap.get(m) == index
    }

    static Boolean isRepeatable(String m, Integer index){
        return methodReapeatableSetsMap.get(m).contains(index)
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
