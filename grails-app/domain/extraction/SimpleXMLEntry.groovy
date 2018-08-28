package extraction

// Literally nothing to do here
class SimpleXMLEntry extends DynamicParserEntry{

    static constraints = {
    }

    @Override
    int compareTo(obj){
        id.compareTo(obj.id)
    }
}
