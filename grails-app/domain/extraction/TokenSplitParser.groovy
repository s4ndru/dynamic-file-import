package extraction

import transformation.TransformationRoutine

class TokenSplitParser extends DynamicParser{

    // Token for splitting lines
    String token

    static hasMany = [linesToIgnore: String]

    static constraints = {
        token(nullable: false)
        name(blank: false)
    }

    // TODO: Test this, since I refactored the code recently
    @Override
    ArrayList<Map<String, Object>> parse(File file) throws ParserUnfitException, ParserInconsistentException {

        // Every entry in this array resembles an object. In our case its a map/dictionary, which we will use for further DB-mapping
        ArrayList<Map<String, Object>> allObjects = new ArrayList<Map<String, String>>()

        // All lines in a file
        file.eachLine{ line ->
            boolean skip = false
            for(String forbiddenLine in linesToIgnore){
                if(line.contains(forbiddenLine)){
                    // We found a line which the user told us to ignore => skip following procedure.
                    skip = true
                    break
                }
            }

            if(!skip) {
                // Split line with the token.
                // ...and trim whitespaces before and after
                def splitLine = line.split("\\s*"+token+"\\s*")

                // Helpermaps -----------------------------
                // Map which represents a single object
                Map<String, Object> objectMap = [:]
                // Map used for finding the correct field for a index in a line
                Map<Integer, ArrayList<Object>> objectMapHelperMap = [:]

                entries.each { entry_it ->
                    if(entry_it.multiple){

                        // TODO test with multiple "multiple"-entries
                        // Helpervariable for "multiple"-Objects
                        Integer multipleCounter = 0

                        entry_it.splitIndizes.each { index_it ->
                            if ((splitLine.size() - 1) >= index_it) {
                                def entry = splitLine[index_it]

                                // Is the entry parseable, if not => Did the user specify something wrong?
                                // If optional, no problemo, but we might need to check the return value,
                                // so we can figure out the right parsing
                                boolean isParseable
                                try {
                                    isParseable = (entry_it.checkType(entry))
                                }
                                catch (ParserUnfitException e) {
                                    if(checkIfStrictParsingNeeded(this.routines, line))
                                        throw e
                                }

                                // Is this entry parseable? Yes?
                                // Add it to our helpermaps, so we can figure out the right entryParser.
                                if (isParseable) {
                                    if (objectMapHelperMap.get(index_it) == null) {
                                        // No fitting parsing for now, add it.
                                        objectMapHelperMap.put(index_it, [entry_it.field, multipleCounter, entry_it.parseField(entry), entry_it.multiple])
                                        multipleCounter++
                                    } else if (objectMapHelperMap.get(index_it)[3] == EntryDatatype.STRING && entry_it.dataType != EntryDatatype.STRING) {
                                        // The previous entryParser is String, and this one is more specific => replace
                                        objectMapHelperMap.put(index_it, [entry_it.field, multipleCounter, entry_it.parseField(entry), entry_it.multiple])
                                        multipleCounter++
                                    } else if (entry_it.dataType != EntryDatatype.STRING && objectMapHelperMap.get(index_it)[3] != EntryDatatype.STRING) {
                                        throw new ParserInconsistentException("Parser is not deterministic! Multiple options to parse a entry")
                                    } else if (entry_it.dataType == objectMapHelperMap.get(index_it)[3]) {
                                        throw new ParserInconsistentException("Parser is not deterministic! Multiple options to parse a entry")
                                    }
                                }
                            }
                        }
                    }
                    else{
                        for(int index in entry_it.splitIndizes) {

                            // Split line. This is how the splitTokenParser works.
                            if ((splitLine.size() - 1) >= index) {
                                def entry = splitLine[index]

                                // TODO: Multiple Entries on same index with optional = true.

                                // Is the entry parseable, if not => User specified something wrong?
                                // If the entry is optional, no problem if not parseable.
                                boolean isParseable
                                try {
                                    isParseable = (entry_it.checkType(entry))
                                }
                                catch (ParserUnfitException e) {
                                    if(checkIfStrictParsingNeeded(this.routines, line))
                                        throw e
                                }

                                // Immediately add the entry for the field. No identical field can occur for this line
                                if (isParseable) {
                                    if (objectMapHelperMap.get(index) == null) {
                                        objectMapHelperMap.put(index, [entry_it.field, null, entry_it.parseField(entry), entry_it.multiple])
                                    } else if (objectMapHelperMap.get(index)[3] == EntryDatatype.STRING && entry_it.dataType != EntryDatatype.STRING) {
                                        objectMapHelperMap.put(index, [entry_it.field, null, entry_it.parseField(entry), entry_it.multiple])
                                    } else if (entry_it.dataType != EntryDatatype.STRING && objectMapHelperMap.get(index)[3] != EntryDatatype.STRING) {
                                        throw new ParserInconsistentException("Parser is not deterministic! Multiple options to parse a entry")
                                    } else if (entry_it.dataType == objectMapHelperMap.get(index)[3]) {
                                        throw new ParserInconsistentException("Parser is not deterministic! Multiple options to parse a entry")
                                    }
                                }
                            }
                        }
                    }
                }

                // Here, we add the (optional) entries, which "won" the priority selection.
                objectMapHelperMap.each{ k, v ->
                    // Parse string to respective datatype
                    String field = v[0]
                    def value = v[2]

                    // Multiple? Add entry as list
                    if(v[3] == true)
                        objectMap.put(field + "${v[1]}", value)
                        //objectMap.put(field + "[${v[1]}]", value)
                    else
                        objectMap.put(field, value)
                }

                // Finished parsing a line, which resembles a object. Add to map.
                allObjects.add(objectMap)
            }
        }

        return allObjects
    }

//    @Override
//    boolean appliesTo(File file) {
//        super.appliesTo(file)
//    }

    String toString() {
        "Tokenparser for " + name
    }
}
