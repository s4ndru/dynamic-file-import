package transformation

class ParamEntry implements Comparable{

    String left_value
    String right_value
//    Object special_value = null

    ParamEntry(String lefty, String righty){
        left_value = lefty
        right_value = righty
    }

/*    ParamEntry(String lefty, Object special){
        left_value = lefty
        special_value = special
    }*/

    static constraints = {
//        special_value nullable: true
        right_value nullable: true, blank: true, validator: { val, obj -> return !(val == null/* && obj.special_value == null */)}
    }

    int compareTo(obj){
        id.compareTo((obj as ParamEntry).id)
    }
}
