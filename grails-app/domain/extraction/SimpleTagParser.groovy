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
    ArrayList<Map<String, Object>> parse(File file) throws ParserUnfitException, ParserInconsistentException {

        ArrayList entries = entries.asList()
        def input = file
        // Every entry in this array resembles an object. In our case its a map/dictionary, which we will use for further DB-mapping
        ArrayList<Map<String, Object>> allObjects = new ArrayList<Map<String, Object>>()
        Map<String, Object> objectMap = [:]
        Map<DynamicParserEntry, Pattern> patterns = [:]
        Map<DynamicParserEntry, Pattern> patternsNoEndTag = [:]
        Map<DynamicParserEntry, Pattern> patternArray = [:]
        Matcher m, m_Multi
        int nestedCounter = 0
        int nestingLevelTemp = nestingLevel
        DynamicParserEntry multilineEntry = null
        StringBuilder multilineConcat = null
        Boolean singleLineFile = false
        int maxSize = 0
        StringBuilder arrayStringBuilder = null
        DynamicParserEntry arrayEntry = null


        for(int i = 0; i < entries.size(); i++)
            for(int j = i + 1; j < entries.size(); j++)
                if(entries[j].startTag.contains(entries[i].startTag))
                    throw new ParserInconsistentException("Simpler start-tag appeared in entry-list that will match tags which are matched by a later defined and more complex start-tag!")


        if(file.readLines().size() == 1 && selectorFileType == AllowedFileType.JSON)
            input = new JSONObject(file.readLines()[0]).toString(2)
        else if(file.readLines().size() == 1 && selectorFileType != AllowedFileType.JSON) {
            if (entries.endTag.contains(null) && entries.size() > 1)
                throw new ParserInconsistentException("We have a file which only has a single line and the parser is defined with more than one entry, " +
                        "but there are entries with no endTag!")

            singleLineFile = true
        }

        // Compile all patterns for later
        entries.each{
            // This is the simplest one, but requires that each tag has its own line
            if(it.endTag == null && it.arraySplitTag == null)
                patterns.put((DynamicParserEntry)it, Pattern.compile("\\Q" + it.startTag + "\\E(.*)"))
            else if(it.arraySplitTag != null){
                patternArray.put((DynamicParserEntry)it, Pattern.compile("\\Q" + it.startTag + "\\E(.*?)\\Q" + it.endTag + "\\E"))
            }
            else{
                patterns.put((DynamicParserEntry)it, Pattern.compile("\\Q" + it.startTag + "\\E(.*?)\\Q" + it.endTag + "\\E"))

                // In case we have a multiline tag
                patternsNoEndTag.put((DynamicParserEntry)it, Pattern.compile("\\Q" + it.startTag + "\\E(.*)"))
            }
        }

        input.eachLine { line, line_index ->

            // only concatenate the line and, if we found the end, set the "flag" back to null
            if(multilineEntry != null){

                if((domainEndTag != null && (line.contains(domainEndTag)) || line.contains(multilineEntry.endTag))) {
					String extractedVal

					if (domainEndTag != null && line.contains(domainEndTag))
						extractedVal = line.subSequence(0, (int)line.indexOf(domainEndTag)).trim()
					else
						extractedVal = line.subSequence(0, (int)line.indexOf(multilineEntry.endTag)).trim()

					if(!extractedVal.isEmpty() && multilineEntry.dataType != EntryDataType.STRING)
						throw new ParserUnfitException("A multi-line spanning value was detected, but the corresponding entry indicates a non-string data type, which is not allowed!")

					multilineConcat.append(extractedVal)

					if(domainStartTag == null && objectMap.get(multilineEntry.field) != null){
						if(objectMap.size() != entries.size()){
							entries.each {
								if(!it.optional && objectMap.get(it.field) == null)
									throw new ParserUnfitException("Required entry for object does not appear in the file above line: " + line_index + "!")
							}
						}
						allObjects.add(objectMap)
						objectMap = [:]

						if(nestingLevel == -1)
							nestingLevelTemp = -1
					}

                    String multilineResult = multilineConcat.toString()
                    objectMap.put(multilineEntry.field, multilineResult)
                    multilineEntry = null

					if(nestingLevelTemp == -1)
						nestingLevelTemp = nestedCounter
                }
                else{
					// If the entry spans over multiple lines and is not a string, then problem! If a number but near domain end, no problem.
					if(multilineEntry.dataType != EntryDataType.STRING)
						throw new ParserUnfitException("A multi-line spanning value was detected, but the corresponding entry indicates a non-string data type, which is not allowed!")

                    multilineConcat.append(line.trim())
					return
                }
            }
            else if(arrayEntry != null){
                // No need to look here for a domainEnd because there always has to be a endTag for the array
                String matchedLine
                if(line.contains(arrayEntry.endTag)){
                    matchedLine = line.replaceAll("(.*)" + Pattern.quote(arrayEntry.endTag), "\$1").trim()

					if(domainStartTag == null && objectMap.get(arrayEntry.field) != null){
						if(objectMap.size() != entries.size()){
							entries.each {
								if(!it.optional && objectMap.get(it.field) == null)
									throw new ParserUnfitException("Required entry for object does not appear in the file above line: " + line_index + "!")
							}
						}
						allObjects.add(objectMap)
						objectMap = [:]

						if(nestingLevel == -1)
							nestingLevelTemp = -1
					}

                    try {
                        if(!matchedLine.isEmpty())
                            arrayEntry.checkType(matchedLine)
                    }
                    catch (ParserUnfitException e) {
						if(checkIfStrictParsingNeeded(this.routines, matchedLine))
							throw new ParserUnfitException(e.message + " parsed by SimpleTagParser: " + this.name, e.cause)
                    }

                    if(!matchedLine.isEmpty())
						arrayStringBuilder.append("|" + matchedLine)

                    objectMap.put(arrayEntry.field, arrayStringBuilder.toString())
                    arrayStringBuilder = null
                    arrayEntry = null

					if(nestingLevelTemp == -1)
						nestingLevelTemp = nestedCounter
                }
                else{
                    matchedLine = line.replaceAll("(.*)" + arrayEntry.arraySplitTag, "\$1").trim()
                    try {
                        arrayEntry.checkType(matchedLine)
                    }
                    catch (ParserUnfitException e) {
						if(checkIfStrictParsingNeeded(this.routines, matchedLine))
							throw new ParserUnfitException(e.message + " parsed by SimpleTagParser: " + this.name, e.cause)
                    }

					if(arrayStringBuilder.toString().isEmpty())
						arrayStringBuilder.append(matchedLine)
					else
						arrayStringBuilder.append("|" + matchedLine)
                }

                return
            }

            // Count if a "domain" starts or ends.
            // Objectmap will be created when we found defined tag and the domainEnd is reached
            if(!singleLineFile && domainStartTag != null && line.contains(domainStartTag))
                nestedCounter++
            else if(!singleLineFile && domainEndTag != null && line.contains(domainEndTag)) {
				if(nestingLevelTemp == nestedCounter && objectMap.size() != 0) {
					if(objectMap.size() != entries.size()){
						entries.each {
							if(!it.optional && objectMap.get(it.field) == null)
								throw new ParserUnfitException("Required entry '" + it.field + "' for object does not appear in the file above line: " + line_index + "!")
						}
					}

                    allObjects.add(objectMap)
                    objectMap = [:]

                    if(nestingLevel == -1)
                        nestingLevelTemp = -1
                }
                else if(nestingLevelTemp > nestedCounter && objectMap.size() != 0)
                    throw new ParserInconsistentException("Parser is below the defined 'nested level'. This happens if there is a 'sub-domain' in the beginning of the domain and the user specified no 'nesting-level'. Or if the user specified a 'nesting-level' higher than it actually is.")

                nestedCounter--

                return
            }

            for(int entry_index = 0; entry_index < this.entries.size(); entry_index++){
                def entry_it = entries.get(entry_index)

//                if(multilineEntry != null)
//                    break

                if(entry_it.arraySplitTag != null)
                    m = patternArray.get(entry_it).matcher((String)line)
                else
                    m = patterns.get(entry_it).matcher((String)line)

                if(entry_it.endTag != null && entry_it.arraySplitTag == null)
                    m_Multi = patternsNoEndTag.get(entry_it).matcher((String)line)


                if(!singleLineFile){
                    // Found regex match and it's not an array, else go to array procedure
                    if(entry_it.arraySplitTag == null && m.find()) {
                        String matchedLine = m.group(1).trim()

                        // Checks if an entry for a object is already parsed, which means it is finished, as a new one starts now.
                        // This only makes sense if "domain tags" are not set.
                        // This is a somewhat risky feature and should only be used when certain that each pseudo-object in a file has all defined key/value pairs.
                        if(domainStartTag == null && objectMap.get(entry_it.field) != null){
                            if(objectMap.size() != entries.size()){
                                entries.each {
                                    if(!it.optional && objectMap.get(it.field) == null)
                                        throw new ParserUnfitException("Required entry '" + it.field + "' for object does not appear in the file above line: " + line_index + "!")
                                }
                            }
                            allObjects.add(objectMap)
                            objectMap = [:]

							if(nestingLevel == -1)
								nestingLevelTemp = -1
                        }

                        try {
                            entry_it.checkType(matchedLine)
                        }
                        catch (ParserUnfitException e) {
							if(checkIfStrictParsingNeeded(this.routines, matchedLine))
                                throw new ParserUnfitException(e.message + " parsed by SimpleTagParser: " + this.name, e.cause)
                        }

                        objectMap.put(entry_it.field, entry_it.parseField(matchedLine))

                        if(nestingLevelTemp == -1)
                            nestingLevelTemp = nestedCounter

                        break
                    }
                    else if(entry_it.arraySplitTag == null && (entry_it.endTag != null || domainEndTag != null) && m_Multi.find()){

						if(nestingLevelTemp == -1)
							nestingLevelTemp = nestedCounter

                        // We found a multilineTag, if the user specified correctly. Now read and concatenate until endtag or domain endtag reached.
                        multilineEntry = entry_it
                        multilineConcat = new StringBuilder("")
                        multilineConcat.append(m_Multi.group(1))
                        break
                    }
                    else if(entry_it.arraySplitTag != null){
                        if(line.contains(entry_it.startTag) && line.contains(entry_it.endTag)) {
                            if (m.find()) {
                                String matchedLine = m.group(1).trim()

								if(domainStartTag == null && objectMap.get(entry_it.field) != null){
									if(objectMap.size() != entries.size()){
										entries.each {
											if(!it.optional && objectMap.get(it.field) == null)
												throw new ParserUnfitException("Required entry '" + it.field + "' for object does not appear in the file above line: " + line_index + "!")
										}
									}
									allObjects.add(objectMap)
									objectMap = [:]

									if(nestingLevel == -1)
										nestingLevelTemp = -1
								}

                                arrayStringBuilder = new StringBuilder()
                                matchedLine.split(entry_it.arraySplitTag).each {
                                    try {
                                        entry_it.checkType(it)
                                    }
                                    catch (ParserUnfitException e) {
										if(checkIfStrictParsingNeeded(this.routines, it))
											throw new ParserUnfitException(e.message + " parsed by SimpleTagParser: " + this.name, e.cause)
                                    }

									if(arrayStringBuilder.toString().isEmpty())
										arrayStringBuilder.append(it)
									else
										arrayStringBuilder.append("|" + it)

                                }
                                objectMap.put(entry_it.field, arrayStringBuilder.toString())
								arrayStringBuilder = null

								if(nestingLevelTemp == -1)
									nestingLevelTemp = nestedCounter

                                break
                            }
                        }
						// Not a single line array
                        else if(line.contains(entry_it.startTag)) {
							arrayStringBuilder = new StringBuilder()
                            arrayEntry = entry_it

                            String matchedLine = line.replaceAll(Pattern.quote(entry_it.startTag) + "(.*)", "\$1")
									.replaceAll("(.*)" + Pattern.quote(entry_it.arraySplitTag), "\$1").trim()

                            try {
                                if(!matchedLine.isEmpty())
                                    entry_it.checkType(matchedLine)
                            }
                            catch (ParserUnfitException e) {
								if(checkIfStrictParsingNeeded(this.routines, matchedLine))
									throw new ParserUnfitException(e.message + " parsed by SimpleTagParser: " + this.name, e.cause)
                            }

                            if(!matchedLine.isEmpty()){
								arrayStringBuilder.append(matchedLine)
							}

                            break
                        }
                    }
                }
                else{
                    for(int i = 0; m.find(); i++){
                        String matchedLine = m.group(1).trim()

                        if(i == maxSize){
                            allObjects.add([:])
                        }

                        if(entry_it.arraySplitTag != null){
							arrayStringBuilder = new StringBuilder()
                            matchedLine.split(entry_it.arraySplitTag).each{
                                try {
                                    entry_it.checkType(matchedLine)
                                }
                                catch (ParserUnfitException e) {
									if(checkIfStrictParsingNeeded(this.routines, matchedLine))
										throw new ParserUnfitException(e.message + " parsed by SimpleTagParser: " + this.name, e.cause)
                                }

								if(arrayStringBuilder.toString().isEmpty())
									arrayStringBuilder.append(it)
								else
									arrayStringBuilder.append(", " + it)

                            }
                            allObjects[i].put(entry_it.field, arrayStringBuilder.toString())
							arrayStringBuilder = null
                        }
                        else{
                            try {
                                entry_it.checkType(matchedLine)
                            }
                            catch (ParserUnfitException e) {
								if(checkIfStrictParsingNeeded(this.routines, matchedLine))
									throw new ParserUnfitException(e.message + " parsed by SimpleTagParser: " + this.name, e.cause)
                            }

                            allObjects[i].put(entry_it.field, entry_it.parseField(matchedLine))
                        }

                        if(maxSize <= i)
                            maxSize = i + 1
                    }
                }
            }

            // If we have no "domain" specified, it means we have a full object if our "objectMap" has as many entries as there are parser entries.
            if(multilineEntry == null && !singleLineFile && domainStartTag == null && objectMap.size() == entries.size()){
                allObjects.add(objectMap)
                objectMap = [:]

				if(nestingLevel == -1)
					nestingLevelTemp = -1
            }
        }

        if(!singleLineFile && domainStartTag == null && objectMap.size() != 0) {
            if(objectMap.size() != entries.size()) {
                entries.each {
                    if (!it.optional && objectMap.get(it.field) == null)
                        throw new ParserUnfitException("Required entry '" + it.field + "' for object does not appear near the end of the file!")
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
