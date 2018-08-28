package transformation


class TransformationProcedure implements Comparable{

    Integer order_id = 0
    Closure transformation_method
    Boolean is_repetitive = true
    Map<String, String> notable_objects = [:]
    Boolean temporary = false

    SortedSet<ParamEntryWrapper> parameterWrappers = new TreeSet<ParamEntryWrapper>()

    static hasMany = [parameterWrappers: ParamEntryWrapper]

    static constraints = {
        transformation_method nullable: false
        parameterWrappers nullable: false
        notable_objects nullable: true
    }

    static mapping = {
        transformation_method sqlType: 'blob'
        sort order_id: "asc"
    }

    int compareTo(obj) {
        // TODO => test with duplicate orders
        order_id.compareTo((obj as TransformationProcedure).order_id)
    }
}
