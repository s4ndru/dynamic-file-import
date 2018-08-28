package extraction

import org.codehaus.groovy.grails.web.json.JSONObject

import java.util.regex.*

class SimpleTagParser extends DynamicParser{

    String domainStartTag = null
    String domainEndTag = null
    int nestingLevel = -1

    static constraints = {
        name(blank: false)
        domainStartTag(blank: false, nullable: true)
        domainEndTag(blank: false, nullable: true, validator: { val, obj ->
            return (val == null && obj.domainStartTag == null) || (val != null && obj.domainStartTag != null)
        })
    }

    @Override
    ArrayList<Map<String, String>> parse(File file) throws ParserUnfitException, ParserInconsistentException {

        ArrayList entries = entries.asList()
        def input = file
        // Every entry in this array resembles an object. In our case its a map/dictionary, which we will use for further DB-mapping
        ArrayList<Map<String, Object>> allObjects = new ArrayList<Map<String, Object>>()
        Map<String, Object> objectMap = [:]
        Map<DynamicParserEntry, Pattern> patterns = [:]
        Map<DynamicParserEntry, Pattern> patternsNoEndTag = [:]
        Map<DynamicParserEntry, Pattern> patternArray = [:]
        Matcher m, m_Multi
        Map<DynamicParserEntry, Boolean> isEntryParsedMap = [:]
        int nestedCounter = 0
        int nestedLevel = nestingLevel
        DynamicParserEntry multilineEntry = null
        StringBuilder multilineConcat = null
        Boolean singleLineFile = false
        int maxSize = 0
        ArrayList arrayObject = null
        DynamicParserEntry arrayEntry = null


        // TODO: test a file where tags contain other tags, but still valid
        for(int i = 0; i < entries.size(); i++)
            for(int j = i + 1; j < entries.size(); j++)
                if(entries[j].startTag.contains(entries[i].startTag))
                    throw new ParserInconsistentException("Simpler Starttag appeared, which covers later tags!")


        if(file.readLines().size() == 1 && selectorFileType == AllowedFiletype.JSON)
            input = new JSONObject(file.readLines()[0]).toString(2)
        else if(file.readLines().size() == 1 && selectorFileType != AllowedFiletype.JSON) {
            if (entries.endTag.contains(null) && entries.size() > 1)
                throw new ParserInconsistentException("We have a file which only has a single line and the parser is defined with more than one entry, " +
                        "but we have entries with no endTag!")

            singleLineFile = true
        }

        // Compile all patterns for later
        entries.each{
            // This is the simplest one, but requires that each tag has its own line
            if(it.endTag == null && it.arraySplitTag == null)
                patterns.put(it, Pattern.compile("(\\Q" + it.startTag + "\\E)(.*)"))
            else if(it.arraySplitTag != null){
                patternArray.put(it, Pattern.compile("(\\Q" + it.startTag + "\\E)(.*?)(\\Q" + it.endTag + "\\E)"))
            }
            else{
                patterns.put(it, Pattern.compile("(\\Q" + it.startTag + "\\E)(.*?)(\\Q" + it.endTag + "\\E)"))

                // In case we have a multiline tag
                patternsNoEndTag.put(it, Pattern.compile("(\\Q" + it.startTag + "\\E)(.*)"))
            }

            isEntryParsedMap.put(it, false)
        }

        input.eachLine { line, line_index ->

            // only concatenate the line and, if we found the end, set the "flag" back to null
            if(multilineEntry != null){

                if(line.contains(domainEndTag) || line.contains(multilineEntry.endTag)){
                    if(line.contains(domainEndTag))
                        multilineConcat.append(line.subSequence(0, (int)line.indexOf(domainEndTag)))
                    else if(line.contains(multilineEntry.endTag))
                        multilineConcat.append(((String)line).subSequence(0, (int)line.indexOf(multilineEntry.endTag)))

                    String multilineResult = multilineConcat.toString().replace("\t", "")
                    objectMap.put(multilineEntry.field, multilineResult)
                    multilineEntry = null
                }
                else{
                    multilineConcat.append(line)
                    return
                }
            }
            else if(arrayEntry != null){
                // No need to look here for a domainEnd because there always has to be a endTag for the array
                String matchedLine
                if(line.contains(arrayEntry.endTag)){
                    matchedLine = line.replace(arrayEntry.endTag, "").trim()
                    try {
                        if(!matchedLine.isEmpty())
                            arrayEntry.checkType(matchedLine)
                    }
                    catch (ParserUnfitException e) {
                        throw new ParserUnfitException(e.message + "parsed by SimpleTagParser: " + this.name, e.cause)
                    }
                    if(!matchedLine.isEmpty())
                        arrayObject.add(arrayEntry.parseField(matchedLine))
                    objectMap.put(arrayEntry.field, arrayObject)
                    arrayObject = null
                    arrayEntry = null
                }
                else{
                    matchedLine = line.replace(arrayEntry.arraySplitTag, "").trim()
                    try {
                        arrayEntry.checkType(matchedLine)
                    }
                    catch (ParserUnfitException e) {
                        throw new ParserUnfitException(e.message + "parsed by SimpleTagParser: " + this.name, e.cause)
                    }
                    arrayObject.add(arrayEntry.parseField(matchedLine))
                }
                return
            }


            // Count if a "domain" starts or ends.
            // Objectmap will be created when we found defined tag and the domainEnd is reached
            if(!singleLineFile && domainStartTag != null && line.contains(domainStartTag))
                nestedCounter++
            else if(!singleLineFile && domainEndTag != null && line.contains(domainEndTag)) {
                if(nestedLevel == nestedCounter && objectMap.size() != 0) {
                    allObjects.add(objectMap)
                    objectMap = [:]
                    if(nestingLevel == -1)
                        nestedLevel = -1
                }
                else if(nestedLevel > nestedCounter && objectMap.size() != 0)
                    throw new ParserInconsistentException("Parser is below the defined 'nested level'. This happens if there is a subdomain in the beginning of the domain and the user specified no domainlevel. Or if the user specified a nestedLevel higher than it actually is.")

                nestedCounter--

                return
            }

            entries.each{ entry_it ->

                if(multilineEntry != null)
                    return

                if(entry_it.arraySplitTag != null)
                    m = patternArray.get(entry_it).matcher((String)line)
                else
                    m = patterns.get(entry_it).matcher((String)line)

                if(entry_it.endTag != null && entry_it.arraySplitTag == null)
                    m_Multi = patternsNoEndTag.get(entry_it).matcher((String)line)


                if(!singleLineFile){
                    // Found regex match and it's not an array, else go to array procedure
                    if(entry_it.arraySplitTag == null && m.find()) {
                        String matchedLine = m.group(2).trim()

                        // Checks if an entry for a object is already parsed. Which means it is finished, as a new one starts now.
                        // This makes only sense, if "domain tags" are not set.
                        // THIS IS ALSO RISKY, USER SHOULD KNOW WHAT HE DOES. See documentation for more info.
                        if(domainStartTag == null && isEntryParsedMap.get(entry_it)){
                            if(objectMap.size() != entries.size()){
                                entries.each {
                                    if(!it.optional && objectMap.get(it.field) == null)
                                        throw new ParserUnfitException("Required entry for object does not appear in the file above line: " + line_index + "!")
                                }
                            }

                            allObjects.add(objectMap)
                            objectMap = [:]

                            entries.each{
                                isEntryParsedMap.put(it, false)
                            }
                        }

                        if(domainStartTag == null)
                            isEntryParsedMap.put(entry_it, true)

                        try {
                            entry_it.checkType(matchedLine)
                        }
                        catch (ParserUnfitException e) {
                            throw new ParserUnfitException(e.message + "parsed by SimpleTagParser: " + this.name, e.cause)
                        }

                        objectMap.put(entry_it.field, entry_it.parseField(matchedLine))
                        if(nestedLevel == -1)
                            nestedLevel = nestedCounter
                    }
                    else if(entry_it.arraySplitTag == null && entry_it.endTag != null && m_Multi.find()){

                        // Problem here, in case we do not have string but end is reached without a endtag. E.g. in Json no endtag because domainend is reached.
                        if(entry_it.dataType != EntryDatatype.STRING)
                            throw new ParserUnfitException("A multiline tag was detected, but the corresponding entry indicates a non-string datatype, which is not allowed!")

                        // We found a multilineTag, if the user specified correctly. Now read and concatenate until endtag reached.
                        multilineEntry = entry_it
                        multilineConcat = new StringBuilder("")
                        multilineConcat.append(m_Multi.group(2))
                        return
                    }
                    // Not a single line array
                    else if(entry_it.arraySplitTag != null){
                        if(line.contains(entry_it.startTag) && line.contains(entry_it.endTag)) {
                            if (m.find()) {
                                String matchedLine = m.group(2).trim()
                                arrayObject = new ArrayList<Object>()
                                matchedLine.split(entry_it.arraySplitTag).each {
                                    try {
                                        entry_it.checkType(it)
                                    }
                                    catch (ParserUnfitException e) {
                                        throw new ParserUnfitException(e.message + "parsed by SimpleTagParser: " + this.name, e.cause)
                                    }
                                    arrayObject.add(entry_it.parseField(it))
                                }
                                objectMap.put(entry_it.field, arrayObject)
                                arrayObject = null
                            }
                        }
                        else if(line.contains(entry_it.startTag)) {
                            arrayObject = new ArrayList<Object>()
                            arrayEntry = entry_it

                            String matchedLine = line.replace(entry_it.startTag, "").replace(entry_it.arraySplitTag, "").trim()

                            try {
                                if(!matchedLine.isEmpty())
                                    entry_it.checkType(matchedLine)
                            }
                            catch (ParserUnfitException e) {
                                throw new ParserUnfitException(e.message + "parsed by SimpleTagParser: " + this.name, e.cause)
                            }

                            if(!matchedLine.isEmpty())
                                arrayObject.add(entry_it.parseField(matchedLine))
                        }
                    }
                }
                else{
                    for(int i = 0; m.find(); i++){
                        String matchedLine = m.group(2).trim()

                        if(i == maxSize){
                            allObjects.add([:])
                        }

                        if(entry_it.arraySplitTag != null){
                            arrayObject = new ArrayList<Object>()
                            matchedLine.split(entry_it.arraySplitTag).each{
                                try {
                                    entry_it.checkType(matchedLine)
                                }
                                catch (ParserUnfitException e) {
                                    throw new ParserUnfitException(e.message + "parsed by SimpleTagParser: " + this.name, e.cause)
                                }
                                arrayObject.add(entry_it.parseField(it))
                            }
                            allObjects[i].put(entry_it.field, arrayObject)
                            arrayObject = null
                        }
                        else{
                            try {
                                entry_it.checkType(matchedLine)
                            }
                            catch (ParserUnfitException e) {
                                throw new ParserUnfitException(e.message + "parsed by SimpleTagParser: " + this.name, e.cause)
                            }
                            allObjects[i].put(entry_it.field, entry_it.parseField(matchedLine))
                        }


                        if(maxSize <= i)
                            maxSize = i + 1
                    }
                }
            }

            // If we have no "domain" specified, it means we have a full object if our "objectMap" has as many entries as there are parserentries.
            if(multilineEntry == null && !singleLineFile && domainStartTag == null && objectMap.size() == entries.size()){
                allObjects.add(objectMap)
                objectMap = [:]

                entries.each{
                    isEntryParsedMap.put(it, false)
                }
            }
        }

        if(!singleLineFile && domainStartTag == null && objectMap.size() != 0) {
            if(objectMap.size() != entries.size()) {
                entries.each {
                    if (!it.optional && objectMap.get(it.field) == null)
                        throw new ParserUnfitException("Required entry for object does not appear near the end of the file!")
                }
            }

            allObjects.add(objectMap)
        }

        return allObjects
    }

    String toString(){
        "Tagparser for " + name
    }
}
