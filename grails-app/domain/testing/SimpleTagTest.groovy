package testing

class SimpleTagTest {
    String value
    String timestamp

    static constraints = {
        value nullable: false
        timestamp nullable: false
    }
}
