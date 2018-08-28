package testing

/**
 * Created by s4ndru on 10/04/2016.
 */
class TokenSplitTest {
    TestFacility facility
    String date
    String product
    String comment
    float summed_pumps
    float meaned_pumps

    static hasMany = [pumps: Float]

    static constraints = {
        comment nullable: true
        facility nullable: true
    }
}
