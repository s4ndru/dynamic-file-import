package extraction

import java.util.regex.*

class SimpleTagParser extends DynamicParser{

    static constraints = {
        name(blank: false)
    }

    @Override
    ArrayList<Map<String, String>> parse(File file) throws ParserUnfitException {

        // Every entry in this array resembles an object. In our case its a map/dictionary, which we will use for further DB-mapping
        ArrayList<Map<String, Object>> allObjects = new ArrayList<Map<String, String>>()
        Map<String, Object> objectMap = [:]
        Map<DynamicParserEntry, Pattern> patterns = [:]
        Matcher m
        Map<DynamicParserEntry, Boolean> isEntryParsedMap = [:]

        entries.each{
            if(it.endTagOptional && it.endTag == null)
                patterns.put(it, Pattern.compile("(" + it.startTag + ")(.*)"))
            else if(it.endTag == null)
                throw new ParserInconsistentException("Parserentry has no non-optional endtag.")
            else
                patterns.put(it, Pattern.compile("(" + it.startTag + ")(.*)(" + it.endTag + ")"))

            isEntryParsedMap.put(it, false)
        }


        // All lines in a file
        file.eachLine { line ->

            // TODO: mit roman korrektheit diskutieren
            entries.each{ entry_it ->
                m = patterns.get(entry_it).matcher(line)
                if(m.find()) {
                    String matchedLine = m.group(2)

                    if(isEntryParsedMap.get(entry_it)){
                        allObjects.add(objectMap)
                        objectMap = [:]

                        entries.each{
                            isEntryParsedMap.put(it, false)
                        }
                    }

                    isEntryParsedMap.put(entry_it, true)

                    try {
                        entry_it.checkType(matchedLine)
                    }
                    catch (ParserUnfitException e) {
                        throw e
                    }

                    objectMap.put(entry_it.field, parseField(entry_it.field, matchedLine))
                }
            }

            if(objectMap.size() == entries.size()){
                allObjects.add(objectMap)
                objectMap = [:]

                entries.each{
                    isEntryParsedMap.put(it, false)
                }
            }
        }

        return allObjects
    }

    String toString() {
        "Tagparser for " + name
    }
}
