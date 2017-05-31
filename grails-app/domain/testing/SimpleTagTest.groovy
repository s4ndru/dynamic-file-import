package testing

class SimpleTagTest {
    String value
    String timestamp

    static constraints = {
        value nullable: true
        timestamp nullable: true
    }
}
