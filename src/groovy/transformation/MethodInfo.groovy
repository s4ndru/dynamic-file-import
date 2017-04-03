package transformation;

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

    static LinkedHashMap<String, Integer> wrapperCountMap =
            ["appendStringLeftToField": 1,
             "appendStringRightToField": 1,
             "splitStringField": 2,
             "appendFields": 2,
             "calculateSum": 2,
             "calculateMean": 2,
             "regexReplace": 2,
             "findAndCacheRelation": 2,
             "createRelation": 2,
             "createNewTemporaryProcedure": 2,
             "crossCreateRelation": 2,
             "crossAddValue": 1,
             "identityTransfer": 1,
             "saveAll": 1,
             "saveAllWhere": 1]

    String propertyName

    MethodInfo(String propertyName) {
        this.propertyName = propertyName
    }

    String toString(){
        return propertyName
    }

    static Integer getWrapperCount(MethodInfo m){
        return wrapperCountMap.get(m.toString())
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
