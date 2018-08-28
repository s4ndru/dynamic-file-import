package testing

class XMLTest {
    Integer address_id
    String comment
    String object_nr_internal
    String phone
    String email

    static constraints = {
        comment nullable: true
        object_nr_internal nullable: true
        phone nullable: true
        email nullable: true
    }
}
