package extraction

class TokenSplitParser extends DynamicParser{

    // Token for splitting lines
    String token

    static hasMany = [linesToIgnore: String]

    static constraints = {
        token(nullable: false)
        name(nullable: false, blank: false)
    }

    @Override
    ArrayList<Map<String, Object>> parse(File file) throws ParserUnfitException, ParserInconsistentException {

        // Every entry in this array resembles an object. In our case its a map/dictionary, which we will use for further DB-mapping
        ArrayList<Map<String, Object>> allObjects = new ArrayList<Map<String, String>>()

        // All lines in a file
        file.eachLine{ line ->
            boolean skip = false
            for(String forbiddenLine in linesToIgnore){
                if(line.contains(forbiddenLine) || line.trim().isEmpty()){
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


                // TODO Doc note => Was thinking if it was safer to limit the max number of splitted entries to the max defined index
                // In the end I decided against it because that would make no sense if indizes of entries in between are not defined anyway. Consistency issue.
//                if(entries.splitIndizes.flatten().max() + 1 < splitLine.size())
//                    throw new ParserUnfitException("Splitted line of file '" + file.name + "' has more entries than the maximum defined index! Please define all possible entries even if they will not be used later.")

                entries.each { entry_it ->
                    // Split line. This is how the splitTokenParser works.
                    if ((splitLine.size() - 1) >= entry_it.splitIndex) {
                        def value = splitLine[entry_it.splitIndex]

                        // Is the entry parseable, if not => User specified something wrong?
                        // If the entry is optional, no problem if not parseable.
                        boolean isParseable
                        try {
                            isParseable = (entry_it.checkType(value))
                        }
                        catch (ParserUnfitException e) {
                            if(checkIfStrictParsingNeeded(this.routines, value))
                                throw e
                        }

                        // Immediately add the value for the field. No identical field can occur for this line
                        if (isParseable) {
                            if (objectMapHelperMap.get(entry_it.splitIndex) == null) {
                                objectMapHelperMap.put(entry_it.splitIndex, [entry_it.field, entry_it.dataType, entry_it.parseField(value)])
                            } else if (objectMapHelperMap.get(entry_it.splitIndex)[1] == EntryDataType.STRING && entry_it.dataType != EntryDataType.STRING) {
                                objectMapHelperMap.put(entry_it.splitIndex, [entry_it.field, entry_it.dataType, entry_it.parseField(value)])
                            } else if (entry_it.dataType != EntryDataType.STRING && objectMapHelperMap.get(entry_it.splitIndex)[1] != EntryDataType.STRING) {
                                throw new ParserInconsistentException("Parser is not deterministic! Multiple options to parse a value")
                            } else if (entry_it.dataType == objectMapHelperMap.get(entry_it.splitIndex)[1]) {
                                throw new ParserInconsistentException("Parser is not deterministic! Multiple options to parse a value")
                            }
                        }
                    }
                }

                // Here we add the (optional) entries which take priority.
                objectMapHelperMap.each{ k, v ->
                    objectMap.put((String)v[0], (Object)v[2])
                }

                // Finished parsing a line, which resembles a object. Add to map.
                allObjects.add(objectMap)
            }
        }

        return allObjects
    }

    String toString() {
        "Token-split-parser for " + name
    }
}
