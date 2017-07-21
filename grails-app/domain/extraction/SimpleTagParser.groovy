package extraction

import org.codehaus.groovy.grails.web.json.JSONObject

import java.util.regex.*

class SimpleTagParser extends DynamicParser{

    String domainStartTag = null
    String domainEndTag = null

    static constraints = {
        name(blank: false)
        domainStartTag(blank: false, nullable: true)
        domainEndTag(blank: false, nullable: true)
    }


    // TODO: parse arrays in files
    // TODO: parse nested level

    @Override
    ArrayList<Map<String, String>> parse(File file) throws ParserUnfitException, ParserInconsistentException {

        def input = file
        // Every entry in this array resembles an object. In our case its a map/dictionary, which we will use for further DB-mapping
        ArrayList<Map<String, Object>> allObjects = new ArrayList<Map<String, Object>>()
        Map<String, Object> objectMap = [:]
        Map<DynamicParserEntry, Pattern> patterns = [:]
        Map<DynamicParserEntry, Pattern> patternsNoEndTag = [:]
        Matcher m, m_Multi
        Map<DynamicParserEntry, Boolean> isEntryParsedMap = [:]
        int nestedCounter = 0
        int nestedLevel = -1
        DynamicParserEntry multilineEntry = null
        StringBuilder multilineConcat = null
        Boolean singleLineFile = false
        int maxSize = 0

        // TODO: check if startTags do not contain each other

        // Both tags have to be set or not set.
        if((domainStartTag != null && domainEndTag == null) || (domainStartTag == null && domainEndTag != null))
            throw new ParserInconsistentException("Domain start and end tag make no sense! Please make sure both are set or both are not set!")


        // TODO: test a file where tags contain other tags, but still valid
        for(int i = 0; i < entries.size(); i++)
            for(int j = i + 1; i < entries.size(); j++)
                if(entries[j].startTag.contains(entries[i].startTag))
                    throw new ParserInconsistentException("Simpler Starttag appeared, which covers later tags!")


        // Compile all patterns for later
        entries.each{
            // This is the simplest one, but requires that each tag has its own line
            if(it.endTag == null)
                patterns.put(it, Pattern.compile("(" + it.startTag + ")(.*)"))
            else{
                patterns.put(it, Pattern.compile("(" + it.startTag + ")(.*?)(" + it.endTag + ")"))

                // In case we have a multiline tag
                patternsNoEndTag.put(it, Pattern.compile("(" + it.startTag + ")(.*)"))
            }
            isEntryParsedMap.put(it, false)
        }

        if(file.readLines().size() == 1 && selectorFileType == AllowedFiletype.JSON)
            input = new JSONObject(file.readLines()[0]).toString(2)
        else if(file.readLines().size() == 1 && selectorFileType != AllowedFiletype.JSON) {
            if (entries.endTag.contains(null) && entries.size() > 1)
                throw new ParserInconsistentException("We have a file which only has a single line and the parser is defined with more than one entry, " +
                        "but we have entries with no endTag!")

            singleLineFile = true
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


            // Count if a "domain" starts or ends.
            // Objectmap will be created when we found defined tag and the domainEnd is reached
            if(!singleLineFile && domainStartTag != null && line.contains(domainStartTag))
                nestedCounter++
            else if(!singleLineFile && domainEndTag != null && line.contains(domainEndTag)) {
                if(nestedLevel == nestedCounter && objectMap.size() != 0) {
                    allObjects.add(objectMap)
                    objectMap = [:]
                    nestedLevel = -1
                }
                else if(nestedLevel >= nestedCounter)
                    throw new ParserInconsistentException("Sorry, we are below the 'nested level'. This should not be possible, we just found a bug. Please contact the developer.")

                nestedCounter--

                return
            }

            entries.each{ entry_it ->

                if(multilineEntry != null)
                    return

                m = patterns.get(entry_it).matcher((String)line)
                if(entry_it.endTag != null)
                    m_Multi = patternsNoEndTag.get(entry_it).matcher((String)line)

                if(!singleLineFile){
                    if(m.find()) {
                        String matchedLine = m.group(2)

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
                    else if(entry_it.endTag != null && m_Multi.find()){

                        if(entry_it.dataType != EntryDatatype.STRING)
                            throw new ParserUnfitException("A multiline tag was detected, but the corresponding entry indicates a non-string datatype, which is not allowed!")

                        // We found a multilineTag, if the user specified correctly. Now read and concatenate until endtag reached.
                        multilineEntry = entry_it
                        multilineConcat = new StringBuilder("")
                        multilineConcat.append(m_Multi.group(2))
                        return
                    }
                }
                else{
                    for(int i = 0; m.find(); i++){
                        String matchedLine = m.group(2)

                        if(i == maxSize){
                            allObjects.add([:])
                        }
                        allObjects[i].put(entry_it.field, entry_it.parseField(matchedLine))

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

    String toString() {
        "Tagparser for " + name
    }
}
