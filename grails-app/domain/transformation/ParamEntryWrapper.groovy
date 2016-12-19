package transformation

class ParamEntryWrapper implements Comparable {

    SortedSet<ParamEntry> parameters = null

    static hasMany = [parameters: ParamEntry]

    def ParamEntryWrapper(){
        parameters = new TreeSet<ParamEntry>()
    }

    def ParamEntryWrapper(SortedSet<ParamEntry> params){
        parameters = params
    }

    def ParamEntryWrapper(ArrayList<ParamEntry> params){
        parameters = new TreeSet<ParamEntry>(params)
    }

    static constraints = {
    }

    static mapping = {
        sort id: "asc"
    }

    int compareTo(obj) {
        id.compareTo((obj as ParamEntryWrapper).id)
    }
}
