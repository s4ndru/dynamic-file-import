package extraction

class SimpleTagEntry extends DynamicParserEntry{

    String startTag
    String endTag = null
    String arraySplitTag = null

    static constraints = {
        startTag(blank: false, nullable: false)
        endTag(blank: false, nullable: true)
        // if we have an array, we need an end
        arraySplitTag nullable: true, validator: { val, obj ->
            return (val != null && obj.endTag != null) || val == null
        }
    }

    @Override
    int compareTo(obj){
        id.compareTo(obj.id)
    }
}
