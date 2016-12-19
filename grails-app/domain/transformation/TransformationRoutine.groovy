package transformation

class TransformationRoutine implements Comparable{

    Integer order_id = 0
    String target_object
    boolean to_update = false
    Map<String, String> update_properties = [:]

    SortedSet<TransformationProcedure> procedures = new TreeSet<TransformationProcedure>()

    static hasMany = [procedures: TransformationProcedure]

    static constraints = {
        target_object nullable: false
//        update_identification_parsingfield nullable: true
//        update_identification_property nullable: true, validator: { val, obj ->
//            return (!obj.to_update && val == null && obj.update_identification_parsingfield == null) ||
//                    (obj.to_update && val != null && obj.update_identification_parsingfield != null)
//        }


        to_update validator: { val, obj ->
            return (!val && obj.update_properties.isEmpty()) || (val && !obj.update_properties.isEmpty())
        }
    }

    static mapping = {
        sort order_id: "asc"
    }

    int compareTo(obj) {
        order_id.compareTo((obj as TransformationRoutine).order_id)
    }
}
