package extraction

import testing.TestingController

class ExtractionController {


    // TODO Move to service

    def parseFiles(){
        //TestingController.bootstrap()

        File dir = new File((String)params.filesPath)
        ArrayList<ArrayList<Map<String, String>>> results = FileParsingService.parseAllFilesInDirectory(dir)

        return
    }

    def createTokenSplitParser(){
        if(TokenSplitParser.findBySelectorNameAndSelectorFileType((String)params.selectorName, AllowedFiletype.fromString((String)params.selectorFileType))) {
            response.sendError(400, "Sorry, a parser with the same 'filename substring' and 'file type' already exists! Please go back and reconsider those parameters.")
            //redirect(url: request.getHeader('referer'), params: params)
            return
        }

        TokenSplitParser tsp = new TokenSplitParser()

        def selectorFileType = AllowedFiletype.fromString(params.selectorFileType)
        params.selectorFileType = null
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
            ArrayList<Integer> indizes = new ArrayList<Integer>()
            params["splitIndizes"+i].split(",").each {indizes.add(Integer.parseInt((String)it))}

            tse_array.add(new TokenSplitEntry(
                    field: params["field" + i],
                    multiple: params["multiple" + i] == "on",
                    trim: params["trim" + i] == "on",
                    optional: params["optional" + i] == "on",
                    dataType: EntryDatatype.fromString((String) params["dataType" + i]),
                    splitIndizes: indizes
            ))
            i++
        }

        tse_array.each({it.save(flush: true)})
        tsp.entries = tse_array
        tsp.save(flush: true)

        redirect(action: "createTokenSplit")
    }

    def createColumnWidthParser(){
        if(ColumnWidthParser.findBySelectorNameAndSelectorFileType((String)params.selectorName, AllowedFiletype.fromString((String)params.selectorFileType))) {
            response.sendError(400, "Sorry, a parser with the same 'filename substring' and 'file type' is already created! Please go back and reconsider those parameters.")
            //redirect(url: request.getHeader('referer'), params: params)
            return
        }

        ColumnWidthParser cwp = new ColumnWidthParser()

        def selectorFileType = AllowedFiletype.fromString(params.selectorFileType)
        params.selectorFileType = null
        cwp.properties = params
        cwp.selectorFileType = selectorFileType

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
                    dataType: EntryDatatype.fromString((String) params["dataType" + i]),
                    columnStart: params["columnStart" + i],
                    columnEnd: params["columnEnd" + i]
            ))
            i++
        }

        cwe_array.each({it.save(flush: true)})
        cwp.entries = cwe_array
        cwp.save(flush: true)
    }
}
