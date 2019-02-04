package testing

import java.sql.Timestamp

class RelationTestClass {

    XML_CD_Catalog_Test relation
    Timestamp timestamp
	String artist

    static hasMany = [relations: XML_CD_Catalog_Test]

    static constraints = {
        relation nullable: true
        timestamp nullable: true
		artist nullable : true
    }
}

