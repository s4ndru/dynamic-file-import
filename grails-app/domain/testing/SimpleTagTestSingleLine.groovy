package testing

class SimpleTagTestSingleLine {
    String url
    Long timestamp
    String task_name
    Integer task_id

    static constraints = {
        url nullable: false
        timestamp nullable: false
        task_name nullable: false
        task_id nullable: false
    }
}
