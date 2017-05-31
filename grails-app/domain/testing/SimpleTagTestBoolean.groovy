package testing

class SimpleTagTestBoolean {
    Float arrivalTime
    Float timestamp
    Boolean boolValue
    String type

    static constraints = {
        arrivalTime nullable: false
        timestamp nullable: false
        boolValue nullable: false
        type nullable: true
    }
}
