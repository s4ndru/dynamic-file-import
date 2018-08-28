package testing

class XMLTest_2 {

    Integer id
    String date_created
    String event_name
    String remote_address
    String session_id
    String switched_username
    String username

    static constraints = {
        date_created nullable: true
        event_name nullable: true
        remote_address nullable: true
        session_id nullable: true
        switched_username nullable: true
        username nullable: true
    }
}
