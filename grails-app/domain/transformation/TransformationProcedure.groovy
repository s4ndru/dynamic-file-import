package transformation

import extraction.DynamicParserEntry


class TransformationProcedure implements Comparable{

    Integer order_id = 0
    Closure transformation_method
    Boolean is_repetitive = true
    Map<String, String> notable_objects = [:]
    List<DynamicParserEntry> created_entries = []
    Boolean temporary = false

    SortedSet<ParamEntryWrapper> parameterWrappers = new TreeSet<ParamEntryWrapper>()

    static hasMany = [parameterWrappers: ParamEntryWrapper, created_entries: DynamicParserEntry]

    static constraints = {
        transformation_method nullable: false
        parameterWrappers nullable: false
        notable_objects nullable: true
        created_entries nullable: true
    }

    static mapping = {
        transformation_method sqlType: 'blob'
        sort order_id: "asc"
    }

    int compareTo(obj) {
        order_id.compareTo((obj as TransformationProcedure).order_id)
    }
}
