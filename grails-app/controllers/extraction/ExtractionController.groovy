package extraction

class ExtractionController {
    StringBuilder errorMessage = new StringBuilder()

    def parseFiles(){

        File dir = new File((String)params.filesPath)
        String resultMessage
        Integer httpCode

        (httpCode, resultMessage) = FileParsingService.parseAllFilesInDirectory(dir)

        render(status: httpCode, text: resultMessage)
    }

    def createTokenSplitParser(){
        if(TokenSplitParser.findBySelectorNameAndSelectorFileType((String)params.selectorName, AllowedFileType.fromString((String)params.selectorFileType))) {
            render(status: 400, text: "A parser with the same 'filename substring' and 'file type' already exists! Please go back and reconsider the parameters.")
            //redirect(url: request.getHeader('referer'), params: params)
            return
        }

        TokenSplitParser tsp = new TokenSplitParser()

        def selectorFileType = AllowedFileType.fromString(params.selectorFileType)
        params.selectorFileType = null
        params.id = null
        tsp.properties = params
        tsp.selectorFileType = selectorFileType

        HashSet<String> linesToIgnore = new HashSet<String>()
        int i = 0
        while(params["lineToIgnore" + i])
            linesToIgnore.add((String)params["lineToIgnore" + i++])

        tsp.linesToIgnore = linesToIgnore

        ArrayList<TokenSplitEntry> tse_array = new ArrayList<TokenSplitEntry>()
        i = 0
        while(params["field" + i]) {
            tse_array.add(new TokenSplitEntry(
                    field: params["field" + i],
                    multiple: params["multiple" + i] == "on",
                    trim: params["trim" + i] == "on",
                    optional: params["optional" + i] == "on",
                    dataType: EntryDataType.fromString((String) params["dataType" + i]),
                    splitIndex: Integer.parseInt((String)params["splitIndex" + i])
            ))
            i++
        }

        if(tse_array.size() == 0){
            render(status: 400, text: "Parser cannot be created without entries! Were some fields not set?")
            return
        }

        tse_array.each{
            it.save(flush: true)
            if(it.hasErrors()){
                it.errors.each { error->
                    errorMessage.append(error.toString() + "\n\r")
                }
            }
        }

        if(!errorMessage.toString().equals("")){
            render(status: 400, text: errorMessage.toString())
            return
        }

        tsp.entries = new TreeSet(tse_array)
        tsp.save(flush: true)

        if(tsp.hasErrors()){
            tsp.errors.each{
                errorMessage.append(it.toString() + "\n\r")
            }

            render(status: 400, text: errorMessage.toString())
            return
        }

        render(status: 200)
    }

    def createColumnWidthParser(){
        if(ColumnWidthParser.findBySelectorNameAndSelectorFileType((String)params.selectorName, AllowedFileType.fromString((String)params.selectorFileType))) {
            response.sendError(400, "Sorry, a parser with the same 'filename substring' and 'file type' is already created! Please go back and reconsider those parameters.")
            //redirect(url: request.getHeader('referer'), params: params)
            return
        }

        ColumnWidthParser cwp = new ColumnWidthParser()

        def selectorFileType = AllowedFileType.fromString(params.selectorFileType)
        params.selectorFileType = null
        params.id = null
        cwp.properties = params
        cwp.selectorFileType = selectorFileType

        // TODO Doc note => Stops if user has an empty entry in between. Careful!
        HashSet<String> linesToIgnore = new HashSet<String>()
        int i = 0
        while(params["lineToIgnore" + i])
            linesToIgnore.add((String)params["lineToIgnore" + i++])

        cwp.linesToIgnore = linesToIgnore

        ArrayList<ColumnWidthEntry> cwe_array = new ArrayList<ColumnWidthEntry>()
        i = 0
        while(params["field" + i]) {
            cwe_array.add(new ColumnWidthEntry(
                    field: params["field" + i],
                    trim: params["trim" + i] == "on",
                    optional: params["optional" + i] == "on",
                    dataType: EntryDataType.fromString((String) params["dataType" + i]),
                    columnStart: params["columnStart" + i],
                    columnEnd: params["columnEnd" + i]
            ))
            i++
        }

        if(cwe_array.size() == 0){
            render(status: 400, text: "Parser cannot be created without entries! Were some fields not set?")
            return
        }

        cwe_array.each{
            it.save(flush: true)
            if(it.hasErrors()){
                it.errors.each { error->
                    errorMessage.append(error.toString() + "\n\r")
                }
            }
        }

        if(!errorMessage.toString().equals("")){
            render(status: 400, text: errorMessage.toString())
            return
        }

        cwp.entries = new TreeSet(cwe_array)
        cwp.save(flush: true)

        if(cwp.hasErrors()){
            cwp.errors.each{
                errorMessage.append(it.toString() + "\n\r")
            }

            render(status: 400, text: errorMessage.toString())
            return
        }

        render(status: 200)
    }

    def createSimpleXMLParser(){
        if(SimpleXMLParser.findBySelectorNameAndSelectorFileType((String)params.selectorName, AllowedFileType.fromString((String)params.selectorFileType))) {
            render(status: 400, text: "A parser with the same 'filename substring' and 'file type' is already created! Please go back and reconsider those parameters.")
            //redirect(url: request.getHeader('referer'), params: params)
            return
        }

        SimpleXMLParser xmlParser = new SimpleXMLParser()

        def selectorFileType = AllowedFileType.fromString(params.selectorFileType)
        params.selectorFileType = null
        params.id = null
        xmlParser.properties = params
        xmlParser.selectorFileType = selectorFileType

       /* if((params.excelTag == "" && (params.startBuffer != "" || params.endBuffer != "")) || (params.excelTag != "" && params.startBuffer == "" && params.endBuffer != "")){
            render(status: 400, text: "'Exceltag' and the 'ignored top supertags' or 'ignored bottom supertags' fields can not be set without each other. Either set 'Exceltag' + 'ignored top supertags' + ('ignored bottom supertags') or none!")
            return
        }*/

        if(params.excelTag == "")
            xmlParser.excelTag = null
        if(params.startBuffer == "")
            xmlParser.startBuffer = 0
        if(params.endBuffer == "")
            xmlParser.endBuffer = 0

        ArrayList<SimpleXMLEntry> xml_array = new ArrayList<SimpleXMLEntry>()
        int i = 0
        while(params["field" + i]) {
            xml_array.add(new SimpleXMLEntry(
                    field: params["field" + i],
                    trim: params["trim" + i] == "on",
                    optional: params["optional" + i] == "on",
                    dataType: EntryDataType.fromString((String) params["dataType" + i])
            ))
            i++
        }

        if(xml_array.size() == 0){
            render(status: 400, text: "Parser cannot be created without entries! Were some fields not set?")
            return
        }

        xml_array.each{
            it.save(flush: true)
            if(it.hasErrors()){
                it.errors.each { error->
                    errorMessage.append(error.toString() + "\n\r")
                }
            }
        }

        if(!errorMessage.toString().equals("")){
            render(status: 400, text: errorMessage.toString())
            return
        }

        xmlParser.entries = new TreeSet(xml_array)
        xmlParser.save(flush:true)

        if(xmlParser.hasErrors()){
            xmlParser.errors.each{
                errorMessage.append(it.toString() + "\n\r")
            }

            render(status: 400, text: errorMessage.toString())
            return
        }

        render(status: 200)
    }

    def createSimpleTagParser(){
        if(SimpleTagParser.findBySelectorNameAndSelectorFileType((String)params.selectorName, AllowedFileType.fromString((String)params.selectorFileType))) {
            render(status: 400, text: "A parser with the same 'filename substring' and 'file type' is already created! Please go back and reconsider those parameters.")
            //redirect(url: request.getHeader('referer'), params: params)
            return
        }

        SimpleTagParser stp = new SimpleTagParser()

        def selectorFileType = AllowedFileType.fromString(params.selectorFileType)
        params.selectorFileType = null
        params.id = null
        if(params.nestingLevel == "")
            params.nestingLevel = -1

        stp.properties = params
        stp.selectorFileType = selectorFileType

        ArrayList<SimpleTagEntry> stp_array = new ArrayList<SimpleTagEntry>()
        int i = 0
        while(params["field" + i]) {
            stp_array.add(new SimpleTagEntry(
                    field: params["field" + i],
                    trim: params["trim" + i] == "on",
                    optional: params["optional" + i] == "on",
                    dataType: EntryDataType.fromString((String) params["dataType" + i]),
                    startTag: params["startTag" + i],
                    endTag: params["endTag" + i] != "" ? params["endTag" + i] : null,
                    arraySplitTag: params["arraySplitTag" + i] != "" ? params["arraySplitTag" + i] : null
            ))
            i++
        }

        if(stp_array.size() == 0){
            render(status: 400, text: "Parser cannot be created without entries! Were some fields not set?")
            return
        }

        stp_array.each{
            it.save(flush: true)
            if(it.hasErrors()){
                it.errors.each { error->
                    errorMessage.append(error.toString() + "\n\r")
                }
            }
        }

        if(!errorMessage.toString().equals("")){
            render(status: 400, text: errorMessage.toString())
            return
        }

        stp.entries = new TreeSet(stp_array)
        stp.save(flush:true)

        if(stp.hasErrors()){
            stp.errors.each{
                errorMessage.append(it.toString() + "\n\r")
            }

            render(status: 400, text: errorMessage.toString())
            return
        }

        render(status: 200)
    }
}
