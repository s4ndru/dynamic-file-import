package extraction

// The TokenSplitEntry represents an line/object in a file, for the TokenSplitParser
class TokenSplitEntry extends DynamicParserEntry{

    boolean multiple = false

    // TODO: check on the initialization service, if there are other splitIndizes when multiple is false or no other indizes when multiple is true
    // TODO: check if the above statement is true.
    // CONT.: After i thought it through, i noticed that multiple splitIndizes without multiple = true just means that an entry can be on more positions
    static hasMany = [splitIndizes: Integer]

    static constraints = {
        splitIndizes(nullable: false)
    }

    @Override
    int compareTo(obj){
        id.compareTo(obj.id)
    }
}
