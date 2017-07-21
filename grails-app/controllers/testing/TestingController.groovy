package testing

import extraction.*
import extraction.AllowedFiletype
import extraction.ColumnWidthEntry
import extraction.ColumnWidthParser
import extraction.DynamicParser
import extraction.EntryDatatype
import extraction.TokenSplitEntry
import extraction.TokenSplitParser
import transformation.ParamEntry
import transformation.ParamEntryWrapper
import transformation.TransformationProcedure
import transformation.TransformationRoutine
import transformation.TransformationService

class TestingController {

    def index(){
        render(view: "/testing/test", params: params)
    }

    def createCWP(){
        render(view: "/testing/CWP_test", params: params)
    }

    def createTokenSplit(){
        render(view: "/testing/Tokensplit_test", params: params)
    }

    def createSimpleTag(){
        //render(view: "/testing/Tokensplit_test", params: params)
    }

    def createProcedure() {
        render(view: "/testing/procedure_test", params: params)
    }

    def createRoutine() {
//        def domainList = grailsApplication.getArtefacts("Domain")*.clazz
//
//        // TODO only remove our parsing stuff, not everything from those packages.
//        domainList.removeAll{it.name.contains("extraction") || it.name.contains("transformation")}
//        params.domainList = []
//        domainList.each{params.domainList.add(it.name)}



        render(view: "/testing/routine_test", params: params)
    }

    // TODO: delete this after we have a working initialization procedure + documentation is finished
    static bootstrap(){

        /*ArrayList<TokenSplitEntry> tse_array = new ArrayList<TokenSplitEntry>()
        tse_array.add(new TokenSplitEntry(field: "facility", splitIndizes: [0], optional: 1, dataType: EntryDatatype.INTEGER))
        tse_array.add(new TokenSplitEntry(field: "false_field", splitIndizes: [0], optional: 1, dataType: EntryDatatype.STRING)) // False entry for testing
        tse_array.add(new TokenSplitEntry(field: "dateCreated", splitIndizes: [1], dataType: EntryDatatype.STRING))
        tse_array.add(new TokenSplitEntry(field: "product", splitIndizes: [2], dataType: EntryDatatype.STRING))
        tse_array.add(new TokenSplitEntry(field: "pumps", splitIndizes: [4,6,8,10,12,14], dataType: EntryDatatype.FLOAT, multiple: true, optional: true))
        tse_array.add(new TokenSplitEntry(field: "comment", splitIndizes: [4,6,8,10,12,14,16], dataType: EntryDatatype.STRING, multiple: false, optional: true))
        tse_array.each({it.save(flush: true)})

        TokenSplitParser tsp = new TokenSplitParser(selectorName: "Avanti_Produktmengen", selectorFileType: AllowedFiletype.TXT,
                name: "Avanti_produktmengen_parser", token: " ", entries: tse_array, linesToIgnore: ["+++ ERDGAS +++", "TS--"],
                description: "A parsing for Files, where each line represents an Object and each entry is separated by a token. e.g. ',', ';', ' '")

        tsp.save(flush: true)

        // TODO check dataType (Seems EntryDatatype.INTEGER gets converted to EntryDatatype.STRING)
        ArrayList<ColumnWidthEntry> cwe_array = new ArrayList<ColumnWidthEntry>()
        cwe_array.add(new ColumnWidthEntry(field: "standort", columnStart: 0, columnEnd: 8, optional: false, dataType: EntryDatatype.STRING, trim: true))
        cwe_array.add(new ColumnWidthEntry(field: "sap", columnStart: 8, columnEnd: 17, optional: false, dataType: EntryDatatype.INTEGER, trim: true))
        cwe_array.add(new ColumnWidthEntry(field: "marke", columnStart: 17, columnEnd: 24, optional: false, dataType: EntryDatatype.STRING, trim: true))
        cwe_array.add(new ColumnWidthEntry(field: "betreiber", columnStart: 24, columnEnd: 47, optional: false, dataType: EntryDatatype.STRING, trim: true))
        cwe_array.add(new ColumnWidthEntry(field: "plz", columnStart: 47, columnEnd: 53, optional: false, dataType: EntryDatatype.INTEGER, trim: true))
        cwe_array.add(new ColumnWidthEntry(field: "ort", columnStart: 53, columnEnd: 77, optional: false, dataType: EntryDatatype.STRING, trim: true))
        cwe_array.add(new ColumnWidthEntry(field: "straße", columnStart: 77, columnEnd: 103, optional: false, dataType: EntryDatatype.STRING, trim: true))
        cwe_array.add(new ColumnWidthEntry(field: "nr", columnStart: 103, columnEnd: 109, optional: false, dataType: EntryDatatype.STRING, trim: true))
        cwe_array.add(new ColumnWidthEntry(field: "modell", columnStart: 109, columnEnd: 118, optional: false, dataType: EntryDatatype.STRING, trim: true))
        cwe_array.add(new ColumnWidthEntry(field: "area_manager_alt", columnStart: 118, columnEnd: 138, optional: false, dataType: EntryDatatype.STRING, trim: true))
        cwe_array.add(new ColumnWidthEntry(field: "area_manager_neu", columnStart: 138, columnEnd: 162, optional: false, dataType: EntryDatatype.STRING, trim: true))
        cwe_array.add(new ColumnWidthEntry(field: "techniker", columnStart: 162, columnEnd: 175, optional: false, dataType: EntryDatatype.STRING, trim: true))

        cwe_array.each({it.save(flush: true)})

        ColumnWidthParser cwp = new ColumnWidthParser(selectorName: "CODOS", selectorFileType: AllowedFiletype.TXT,
                name: "CODOS_parser", entries: cwe_array, // linesToIgnore: ["Standort   SAP  "],
                description: "A parsing for Files, where each Column has a fixed width")



        TransformationProcedure tp_cwp2 = new TransformationProcedure(order_id: 1, transformation_method: TransformationService.&identityTransfer,
                is_repetitive: false, notable_objects: ["area_manager_neu" : "** AM NEU **"])
        ArrayList<ParamEntry> paramList_cwp = new ArrayList<ParamEntry>()
        paramList_cwp.add(new ParamEntry("name", "area_manager_neu"))
        paramList_cwp.each{it.save(flush: true)}
        ParamEntryWrapper pw = new ParamEntryWrapper(paramList_cwp)
        pw.save(flush: true)
        tp_cwp2.parameterWrappers.add(pw)
        tp_cwp2.save(flush: true)
        TransformationRoutine tr_cwp2 = new TransformationRoutine(order_id: 1, target_object: "TestResponsibility")
        tr_cwp2.procedures.add(tp_cwp2)
        tr_cwp2.save(flush: true)
        cwp.routines.add(tr_cwp2)


        // Create Address and Contact once /////////////////////////////////////////////////////////////////////////////
        TransformationProcedure tp_cwp1 = new TransformationProcedure(order_id: 1, transformation_method: TransformationService.&splitStringField,
                notable_objects: ["area_manager_neu" : "** AM NEU **"])
        paramList_cwp.clear()
        paramList_cwp.add(new ParamEntry("techniker", ","))
        paramList_cwp.each{it.save(flush: true)}
        pw = new ParamEntryWrapper(paramList_cwp)
        pw.save(flush: true)
        tp_cwp1.parameterWrappers.add(pw)
        paramList_cwp.clear()
        paramList_cwp.add(new ParamEntry("nachname", "0"))
        paramList_cwp.add(new ParamEntry("vorname", "1"))
        paramList_cwp.each{it.save(flush: true)}
        pw = new ParamEntryWrapper(paramList_cwp)
        pw.save(flush: true)
        tp_cwp1.parameterWrappers.add(pw)
        tp_cwp1.save(flush: true)
        tr_cwp2 = new TransformationRoutine(order_id: 2, target_object: "TestAddress", to_update: true, update_properties: ["vorname": "vorname", "nachname": "nachname"])
        tr_cwp2.procedures.add(tp_cwp1)
        tr_cwp2.save(flush: true)

        // Delete after creation   /////////////////////////////////////////////////////////////////////////////////////
        tp_cwp2 = new TransformationProcedure(order_id: 2, transformation_method: TransformationService.&identityTransfer,
                notable_objects: ["area_manager_neu" : "** AM NEU **"])
        paramList_cwp = new ArrayList<ParamEntry>()
        paramList_cwp.add(new ParamEntry("vorname", "vorname"))
        paramList_cwp.add(new ParamEntry("nachname", "nachname"))
        paramList_cwp.each{it.save(flush: true)}
        pw = new ParamEntryWrapper(paramList_cwp)
        pw.save(flush: true)
        tp_cwp2.parameterWrappers.add(pw)
        tp_cwp2.save(flush: true)
        tr_cwp2.procedures.add(tp_cwp2)

        tp_cwp2 = new TransformationProcedure(order_id: 3, transformation_method: TransformationService.&findAndCacheRelation,
                notable_objects: ["area_manager_neu" : "** AM NEU **"])
        paramList_cwp = new ArrayList<ParamEntry>()
        paramList_cwp.add(new ParamEntry("address", "TestAddress"))
        paramList_cwp.each{it.save(flush: true)}
        pw = new ParamEntryWrapper(paramList_cwp)
        pw.save(flush: true)
        tp_cwp2.parameterWrappers.add(pw)
        paramList_cwp.clear()
        paramList_cwp.add(new ParamEntry("vorname", "vorname"))
        paramList_cwp.add(new ParamEntry("nachname", "nachname"))
        paramList_cwp.each{it.save(flush: true)}
        pw = new ParamEntryWrapper(paramList_cwp)
        pw.save(flush: true)
        tp_cwp2.parameterWrappers.add(pw)
        tp_cwp2.save(flush: true)
        tr_cwp2.procedures.add(tp_cwp2)
        tr_cwp2.save(flush: true)
        cwp.routines.add(tr_cwp2)

        // Delete after creation   /////////////////////////////////////////////////////////////////////////////////////
        tp_cwp2 = new TransformationProcedure(order_id: 1, transformation_method: TransformationService.&identityTransfer,
                notable_objects: ["area_manager_neu" : "** AM NEU **"])
        paramList_cwp = new ArrayList<ParamEntry>()
        paramList_cwp.add(new ParamEntry("address", "address"))
        paramList_cwp.each{it.save(flush: true)}
        pw = new ParamEntryWrapper(paramList_cwp)
        pw.save(flush: true)
        tp_cwp2.parameterWrappers.add(pw)
        tp_cwp2.save(flush: true)
        tr_cwp2 = new TransformationRoutine(order_id: 3, target_object: "TestContact", to_update: true, update_properties: ["address": "address"])
        tr_cwp2.procedures.add(tp_cwp2)


        tp_cwp2 = new TransformationProcedure(order_id: 2, transformation_method: TransformationService.&findAndCacheRelation,
                is_repetitive: true, notable_objects: ["area_manager_neu" : "** AM NEU **"])
        paramList_cwp = new ArrayList<ParamEntry>()
        paramList_cwp.add(new ParamEntry("contact", "TestContact"))
        paramList_cwp.each{it.save(flush: true)}
        pw = new ParamEntryWrapper(paramList_cwp)
        pw.save(flush: true)
        tp_cwp2.parameterWrappers.add(pw)
        paramList_cwp.clear()
        paramList_cwp.add(new ParamEntry("address", "address"))
        // Note gernot: add here the "companySiteID" paramEntry
        paramList_cwp.each{it.save(flush: true)}
        pw = new ParamEntryWrapper(paramList_cwp)
        pw.save(flush: true)
        tp_cwp2.parameterWrappers.add(pw)
        tp_cwp2.save(flush: true)
        tr_cwp2.procedures.add(tp_cwp2)
        tr_cwp2.save(flush: true)
        cwp.routines.add(tr_cwp2)


        TransformationProcedure tp4 = new TransformationProcedure(order_id: 1, transformation_method: TransformationService.&createNewTemporaryProcedure,
                is_repetitive: false, notable_objects: ["area_manager_neu" : "** AM NEU **"])
        paramList_cwp.clear()
        //create process
        paramList_cwp.add(new ParamEntry("crossCreateRelation", "true"))
        paramList_cwp.add(new ParamEntry("area_manager_neu", "** AM NEU **"))
        paramList_cwp.each{it.save(flush: true)}
        pw = new ParamEntryWrapper(paramList_cwp)
        pw.save(flush: true)
        tp4.parameterWrappers.add(pw)
        paramList_cwp.clear()
        //find by value in datum for field
        //eg look in ResponsibilityKind property name if entry  with datum-value with datum-key "area_manager_neu" exists
        paramList_cwp.add(new ParamEntry("name", "area_manager_neu"))
        paramList_cwp.each{it.save(flush: true)}
        pw = new ParamEntryWrapper(paramList_cwp)
        pw.save(flush: true)
        tp4.parameterWrappers.add(pw)
        paramList_cwp.clear()
        paramList_cwp.add(new ParamEntry("responsibility", "TestResponsibility"))
        paramList_cwp.each{it.save(flush: true)}
        pw = new ParamEntryWrapper(paramList_cwp)
        pw.save(flush: true)
        tp4.parameterWrappers.add(pw)
        tp4.save(flush: true)
        TransformationRoutine tr_cwp1 = new TransformationRoutine(order_id: 4, target_object: "ColumnWidthTest")
        tr_cwp1.procedures.add(tp4)

        tp_cwp1 = new TransformationProcedure(order_id: 2, transformation_method: TransformationService.&splitStringField,
                notable_objects: ["area_manager_neu" : "** AM NEU **"])
        paramList_cwp.clear()
        paramList_cwp.add(new ParamEntry("techniker", ","))
        paramList_cwp.each{it.save(flush: true)}
        pw = new ParamEntryWrapper(paramList_cwp)
        pw.save(flush: true)
        tp_cwp1.parameterWrappers.add(pw)
        paramList_cwp.clear()
        paramList_cwp.add(new ParamEntry("nachname", "0"))
        paramList_cwp.add(new ParamEntry("vorname", "1"))
        paramList_cwp.each{it.save(flush: true)}
        pw = new ParamEntryWrapper(paramList_cwp)
        pw.save(flush: true)
        tp_cwp1.parameterWrappers.add(pw)
        tp_cwp1.save(flush: true)
        tr_cwp1.procedures.add(tp_cwp1)
        tr_cwp1.save(flush: true)

        tp_cwp1 = new TransformationProcedure(order_id: 3, transformation_method: TransformationService.&identityTransfer,
                notable_objects: ["area_manager_neu" : "** AM NEU **"])
        paramList_cwp.clear()
        // TODO: write a notice in the document => paramList has to be a ArrayList at first, because a TreeSet immediately works with "compareTo()"
        // "compareTo()" can not be called though, because ParamEntry compares with it's ID, which does not exist when the object is not persisted
        //ParamEntry(ObjectAttributeName, ImportAttributeName)
        paramList_cwp.add(new ParamEntry("standort", "standort"))
        paramList_cwp.add(new ParamEntry("sap", "sap"))
        paramList_cwp.add(new ParamEntry("marke", "marke"))
        paramList_cwp.add(new ParamEntry("betreiber", "betreiber"))
        paramList_cwp.add(new ParamEntry("plz", "plz"))
        paramList_cwp.add(new ParamEntry("ort", "ort"))
        paramList_cwp.add(new ParamEntry("straße", "straße"))
        paramList_cwp.add(new ParamEntry("nr", "nr"))
        paramList_cwp.add(new ParamEntry("modell", "modell"))
        paramList_cwp.add(new ParamEntry("area_manager_alt", "area_manager_alt"))
        paramList_cwp.add(new ParamEntry("area_manager_neu", "area_manager_neu"))
        paramList_cwp.add(new ParamEntry("techniker_vorname", "vorname"))
        paramList_cwp.add(new ParamEntry("techniker_nachname", "nachname"))
        paramList_cwp.add(new ParamEntry("contact", "contact"))
        paramList_cwp.each{it.save(flush: true)}
        pw = new ParamEntryWrapper(paramList_cwp)
        pw.save(flush: true)
        tp_cwp1.parameterWrappers.add(pw)
        tp_cwp1.save(flush: true)
        tr_cwp1.procedures.add(tp_cwp1)
        tr_cwp1.save(flush: true)
        cwp.routines.add(tr_cwp1)


        tp4 = new TransformationProcedure(order_id: 1, transformation_method: TransformationService.&createNewTemporaryProcedure,
                is_repetitive: false, notable_objects: ["sap" : "1626730"])
        paramList_cwp.clear()
        paramList_cwp.add(new ParamEntry("crossAddValue", "true"))
        paramList_cwp.add(new ParamEntry("area_manager_neu", "** AM NEU **"))
        paramList_cwp.each{it.save(flush: true)}
        pw = new ParamEntryWrapper(paramList_cwp)
        pw.save(flush: true)
        tp4.parameterWrappers.add(pw)
        paramList_cwp.clear()
        paramList_cwp.add(new ParamEntry("contact_plz", "plz"))
        paramList_cwp.each{it.save(flush: true)}
        pw = new ParamEntryWrapper(paramList_cwp)
        pw.save(flush: true)
        tp4.parameterWrappers.add(pw)
        tp4.save(flush: true)
        TransformationRoutine tr_cwp3 = new TransformationRoutine(order_id: 5, target_object: "ColumnWidthTest",
                update_properties: ["standort" : "standort"], to_update: true)
        tr_cwp3.procedures.add(tp4)
        tr_cwp3.save(flush: true)
        cwp.routines.add(tr_cwp3)


        //ColumnWidthParser cwp = ColumnWidthParser.findByName("CODOS_parser")
        //cwp.linesToIgnore = ["SAP"]
        cwp.save(flush: true)


        TransformationProcedure tp = new TransformationProcedure(order_id: 1, transformation_method: TransformationService.&identityTransfer)
        ArrayList<ParamEntry> paramList = new ArrayList<ParamEntry>()
//        paramList.add(new ParamEntry("facility", "facility"))
        paramList.add(new ParamEntry("date", "dateCreated"))
        paramList.add(new ParamEntry("product", "product"))
        paramList.add(new ParamEntry("comment", "comment"))
        paramList.add(new ParamEntry("summed_pumps", "sum"))
        paramList.add(new ParamEntry("meaned_pumps", "mean"))
        paramList.add(new ParamEntry("pumps", "pumps0"))
        paramList.add(new ParamEntry("pumps", "pumps1"))
        paramList.add(new ParamEntry("pumps", "pumps2"))
        paramList.add(new ParamEntry("pumps", "pumps3"))
        paramList.add(new ParamEntry("pumps", "pumps4"))
        paramList.add(new ParamEntry("pumps", "pumps5"))
        paramList.each{it.save(flush: true)}
        pw = new ParamEntryWrapper(paramList)
        pw.save(flush: true)
        tp.parameterWrappers.add(pw)
        tp.save(flush: true)
        TransformationRoutine tr = new TransformationRoutine(order_id: 2, is_repetitive: true, notable_objects: null, target_object: "TokenSplitTest", procedures: [tp])
        tr.save(flush: true)


        TransformationProcedure tp1 = new TransformationProcedure(order_id: 1, transformation_method: TransformationService.&appendStringLeftToField)
        paramList = new ArrayList<ParamEntry>()
        ParamEntry pe1 = new ParamEntry("facility", "A")
        pe1.save(flush: true)
        paramList.add(pe1)
        pw = new ParamEntryWrapper(paramList)
        pw.save(flush: true)
        tp1.parameterWrappers.add(pw)
        tp1.save(flush: true)


        TransformationProcedure tp12 = new TransformationProcedure(order_id: 2, transformation_method: TransformationService.&calculateSum)
        paramList.clear()
        pe1 = new ParamEntry("sum", "")
        pe1.save(flush: true)
        paramList.add(pe1)
        pw = new ParamEntryWrapper(paramList)
        pw.save(flush: true)
        tp12.parameterWrappers.add(pw)
        paramList.clear()
        pe1 = new ParamEntry("pumps0", "pumps1")
        pe1.save(flush: true)
        paramList.add(pe1)
        pe1 = new ParamEntry("pumps2", "pumps3")
        pe1.save(flush: true)
        paramList.add(pe1)
        pe1 = new ParamEntry("pumps4", "pumps5")
        pe1.save(flush: true)
        paramList.add(pe1)
        pw = new ParamEntryWrapper(paramList)
        pw.save(flush: true)
        tp12.parameterWrappers.add(pw)
        tp12.save(flush: true)


        TransformationProcedure tp21 = new TransformationProcedure(order_id: 3, transformation_method: TransformationService.&calculateMean)
        paramList.clear()
        pe1 = new ParamEntry("mean", "")
        pe1.save(flush: true)
        paramList.add(pe1)
        pw = new ParamEntryWrapper(paramList)
        pw.save(flush: true)
        tp21.parameterWrappers.add(pw)
        paramList.clear()
        pe1 = new ParamEntry("pumps0", "pumps1")
        pe1.save(flush: true)
        paramList.add(pe1)
        pe1 = new ParamEntry("pumps2", "pumps3")
        pe1.save(flush: true)
        paramList.add(pe1)
        pe1 = new ParamEntry("pumps4", "pumps5")
        pe1.save(flush: true)
        paramList.add(pe1)
        pw = new ParamEntryWrapper(paramList)
        pw.save(flush: true)
        tp21.parameterWrappers.add(pw)
        tp21.save(flush: true)


        TransformationProcedure tp2 = new TransformationProcedure(order_id: 4, transformation_method: TransformationService.&identityTransfer)
        paramList = new ArrayList<ParamEntry>()
        ParamEntry pe2 = new ParamEntry("objectNrInternal", "facility")
        pe2.save(flush: true)
        paramList.add(pe2)
        ParamEntry pe22 = new ParamEntry("product", "product")
        pe22.save(flush: true)
        paramList.add(pe22)
        pw = new ParamEntryWrapper(paramList)
        pw.save(flush: true)
        tp2.parameterWrappers.add(pw)
        tp2.save(flush: true)


        TransformationRoutine tr2 = new TransformationRoutine(order_id: 1, is_repetitive: true, notable_objects: null,
                target_object: "TestFacility", procedures: [tp1, tp2, tp12, tp21])
        tr2.save(flush: true)


        TransformationProcedure tp3 = new TransformationProcedure(order_id: 2, transformation_method: TransformationService.&createRelation)
        paramList = new TreeSet<ParamEntry>()
        ParamEntry pe3 = new ParamEntry("facility", "TestFacility")
        pe3.save(flush: true)
        paramList.add(pe3)
        pw = new ParamEntryWrapper(paramList)
        pw.save(flush: true)
        tp3.parameterWrappers.add(pw)
        paramList.clear()
        ParamEntry pe4 = new ParamEntry("objectNrInternal", "facility")
        pe4.save(flush: true)
        paramList.add(pe4)
        ParamEntry pe5 = new ParamEntry("product", "product")
        pe5.save(flush: true)
        paramList.add(pe5)
        pw = new ParamEntryWrapper(paramList)
        pw.save(flush: true)
        tp3.parameterWrappers.add(pw)
        tp3.save(flush: true)
        tr.procedures.add(tp3)
        tr.save(flush: true)


        TokenSplitParser temp = DynamicParser.get(1) as TokenSplitParser
        temp.routines.add(tr)
        temp.routines.add(tr2)
        temp.save(flush: true)*/

        ArrayList<SimpleTagEntry> stp_array = new ArrayList<SimpleTagEntry>()
        stp_array.add(new SimpleTagEntry(field: "custom_value", dataType: EntryDatatype.STRING, startTag: "\"custom_value\": \"", endTag: "\",", optional: true))
        stp_array.add(new SimpleTagEntry(field: "timestamp", dataType: EntryDatatype.STRING, startTag: "\"timestamp\": \"" , endTag: "\""))

        SimpleTagParser stp = new SimpleTagParser(selectorName: "json", selectorFileType: AllowedFiletype.TXT, name: "Jsonparser", description: "Tagparser for simple json files.", entries: stp_array)

        TransformationProcedure stp_tp = new TransformationProcedure(order_id: 0, transformation_method: TransformationService.&identityTransfer)
        TreeSet<ParamEntry> stp_params = new TreeSet<ParamEntry>()
        ParamEntry stp_pe = new ParamEntry("value", "custom_value")
        stp_pe.save(flush: true)
        stp_params.add(stp_pe)
        stp_pe = new ParamEntry("timestamp", "timestamp")
        stp_pe.save(flush: true)
        stp_params.add(stp_pe)

        ParamEntryWrapper stp_pw = new ParamEntryWrapper(stp_params)
        stp_pw.save(flush: true)
        stp_tp.parameterWrappers.add(stp_pw)

        stp_tp.save(flush: true)

        TransformationRoutine stp_tr = new TransformationRoutine(order_id: 1, is_repetitive: true, notable_objects: null,
                target_object: "SimpleTagTest", procedures: [stp_tp])
        stp_tr.save(flush: true)

        stp.routines.add(stp_tr)
        stp.save(flush:true)



        stp_array = new ArrayList<SimpleTagEntry>()
        stp_array.add(new SimpleTagEntry(field: "arrivalTime", dataType: EntryDatatype.FLOAT, startTag: "\"arrivalTime\": ", endTag: ","))
        stp_array.add(new SimpleTagEntry(field: "boolValue", dataType: EntryDatatype.BOOLEAN, startTag: "\"boolValue\": " , endTag: ","))
        stp_array.add(new SimpleTagEntry(field: "timestamp", dataType: EntryDatatype.FLOAT, startTag: "\"timestamp\": "))

        stp = new SimpleTagParser(selectorName: "environment", selectorFileType: AllowedFiletype.JSON,
                name: "Jsonparser 2", description: "Tagparser json file with bool value.", entries: stp_array, domainStartTag: "{", domainEndTag: "}")

        stp_tp = new TransformationProcedure(order_id: 0, transformation_method: TransformationService.&identityTransfer)
        stp_params = new TreeSet<ParamEntry>()
        stp_pe = new ParamEntry("arrivalTime", "arrivalTime")
        stp_pe.save(flush: true)
        stp_params.add(stp_pe)
        stp_pe = new ParamEntry("timestamp", "timestamp")
        stp_pe.save(flush: true)
        stp_params.add(stp_pe)
        stp_pe = new ParamEntry("boolValue", "boolValue")
        stp_pe.save(flush: true)
        stp_params.add(stp_pe)

        stp_pw = new ParamEntryWrapper(stp_params)
        stp_pw.save(flush: true)
        stp_tp.parameterWrappers.add(stp_pw)

        stp_tp.save(flush: true)

        stp_tr = new TransformationRoutine(order_id: 1, is_repetitive: true, notable_objects: null,
                target_object: "SimpleTagTestBoolean", procedures: [stp_tp])
        stp_tr.save(flush: true)

        stp.routines.add(stp_tr)
        stp.save(flush:true)


        // task_id, task_name, timestamp, url

        stp_array = new ArrayList<SimpleTagEntry>()
        stp_array.add(new SimpleTagEntry(field: "task_id", dataType: EntryDatatype.INTEGER, startTag: "\"task_id\": ", endTag: ","))
        stp_array.add(new SimpleTagEntry(field: "task_name", dataType: EntryDatatype.STRING, startTag: "\"task_name\": \"" , endTag: "\","))
        stp_array.add(new SimpleTagEntry(field: "timestamp", dataType: EntryDatatype.LONG, startTag: "\"timestamp\": ", endTag: ","))
        stp_array.add(new SimpleTagEntry(field: "url", dataType: EntryDatatype.STRING, startTag: "\"url\": \"", endTag: "\","))

        stp = new SimpleTagParser(selectorName: "queries", selectorFileType: AllowedFiletype.JSON, name: "Jsonparser 2",
                description: "Tagparser for file which only has a single line with all entries.", entries: stp_array, domainStartTag: "{", domainEndTag: "}")

        stp_tp = new TransformationProcedure(order_id: 0, transformation_method: TransformationService.&identityTransfer)
        stp_params = new TreeSet<ParamEntry>()
        stp_pe = new ParamEntry("task_id", "task_id")
        stp_pe.save(flush: true)
        stp_params.add(stp_pe)
        stp_pe = new ParamEntry("timestamp", "timestamp")
        stp_pe.save(flush: true)
        stp_params.add(stp_pe)
        stp_pe = new ParamEntry("task_name", "task_name")
        stp_pe.save(flush: true)
        stp_params.add(stp_pe)
        stp_pe = new ParamEntry("url", "url")
        stp_pe.save(flush: true)
        stp_params.add(stp_pe)

        stp_pw = new ParamEntryWrapper(stp_params)
        stp_pw.save(flush: true)
        stp_tp.parameterWrappers.add(stp_pw)

        stp_tp.save(flush: true)

        stp_tr = new TransformationRoutine(order_id: 1, is_repetitive: true, notable_objects: null,
                target_object: "SimpleTagTestSingleLine", procedures: [stp_tp])
        stp_tr.save(flush: true)

        stp.routines.add(stp_tr)
        stp.save(flush:true)

        stp_array = new ArrayList<SimpleTagEntry>()
        stp_array.add(new SimpleTagEntry(field: "task_id", dataType: EntryDatatype.INTEGER, startTag: "\"task_id\": ", endTag: ","))
        stp_array.add(new SimpleTagEntry(field: "task_name", dataType: EntryDatatype.STRING, startTag: "\"task_name\": \"" , endTag: "\","))
        stp_array.add(new SimpleTagEntry(field: "timestamp", dataType: EntryDatatype.LONG, startTag: "\"timestamp\": ", endTag: ","))
        stp_array.add(new SimpleTagEntry(field: "url", dataType: EntryDatatype.STRING, startTag: "\"url\": \"", endTag: "\","))

        stp = new SimpleTagParser(selectorName: "queries", selectorFileType: AllowedFiletype.TXT, name: "Jsonparser txt-testversion",
                description: "Tagparser for file which only has a single line with all entries.", entries: stp_array)

        stp_tp = new TransformationProcedure(order_id: 0, transformation_method: TransformationService.&identityTransfer)
        stp_params = new TreeSet<ParamEntry>()
        stp_pe = new ParamEntry("task_id", "task_id")
        stp_pe.save(flush: true)
        stp_params.add(stp_pe)
        stp_pe = new ParamEntry("timestamp", "timestamp")
        stp_pe.save(flush: true)
        stp_params.add(stp_pe)
        stp_pe = new ParamEntry("task_name", "task_name")
        stp_pe.save(flush: true)
        stp_params.add(stp_pe)
        stp_pe = new ParamEntry("url", "url")
        stp_pe.save(flush: true)
        stp_params.add(stp_pe)

        stp_pw = new ParamEntryWrapper(stp_params)
        stp_pw.save(flush: true)
        stp_tp.parameterWrappers.add(stp_pw)

        stp_tp.save(flush: true)

        stp_tr = new TransformationRoutine(order_id: 1, is_repetitive: true, notable_objects: null,
                target_object: "SimpleTagTestSingleLine", procedures: [stp_tp])
        stp_tr.save(flush: true)

        stp.routines.add(stp_tr)
        stp.save(flush:true)

        stp_array = new ArrayList<SimpleTagEntry>()
        stp_array.add(new SimpleTagEntry(field: "arrivalTime", dataType: EntryDatatype.FLOAT, startTag: "\"arrivalTime\": ", endTag: ","))
        stp_array.add(new SimpleTagEntry(field: "boolValue", dataType: EntryDatatype.BOOLEAN, startTag: "\"boolValue\": " , endTag: ","))
        stp_array.add(new SimpleTagEntry(field: "timestamp", dataType: EntryDatatype.FLOAT, startTag: "\"timestamp\": "))
        stp_array.add(new SimpleTagEntry(field: "type", dataType: EntryDatatype.STRING, startTag: "\"type\": \"", endTag: "\","))

        stp = new SimpleTagParser(selectorName: "multiline", selectorFileType: AllowedFiletype.JSON,
                name: "Jsonparser 2", description: "Tagparser json multiline file", entries: stp_array, domainStartTag: "{", domainEndTag: "}")

        stp_tp = new TransformationProcedure(order_id: 0, transformation_method: TransformationService.&identityTransfer)
        stp_params = new TreeSet<ParamEntry>()
        stp_pe = new ParamEntry("arrivalTime", "arrivalTime")
        stp_pe.save(flush: true)
        stp_params.add(stp_pe)
        stp_pe = new ParamEntry("timestamp", "timestamp")
        stp_pe.save(flush: true)
        stp_params.add(stp_pe)
        stp_pe = new ParamEntry("boolValue", "boolValue")
        stp_pe.save(flush: true)
        stp_params.add(stp_pe)
        stp_pe = new ParamEntry("type", "type")
        stp_pe.save(flush: true)
        stp_params.add(stp_pe)

        stp_pw = new ParamEntryWrapper(stp_params)
        stp_pw.save(flush: true)
        stp_tp.parameterWrappers.add(stp_pw)

        stp_tp.save(flush: true)

        stp_tr = new TransformationRoutine(order_id: 1, is_repetitive: true, notable_objects: null,
                target_object: "SimpleTagTestBoolean", procedures: [stp_tp])
        stp_tr.save(flush: true)

        stp.routines.add(stp_tr)
        stp.save(flush:true)

        return
    }

}
