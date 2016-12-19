package testing

class ColumnWidthTest {

    String standort
    Integer sap
    String marke
    String betreiber
    Integer plz
    String ort
    String straße
    String nr
    String modell
    String area_manager_alt
    String area_manager_neu
    String techniker_vorname
    String techniker_nachname
    TestResponsibility responsibility
    Integer contact_plz
    TestContact contact

    static constraints = {
        responsibility nullable: true
        contact_plz nullable: true
        contact nullable: true
        techniker_vorname nullable: true
        techniker_nachname nullable: true
    }
}
