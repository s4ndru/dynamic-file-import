package extraction

class ColumnWidthParser extends DynamicParser{

    static hasMany = [linesToIgnore: String]

    static constraints = {
        name(blank: false)
    }

    @Override
    ArrayList<Map<String, Object>> parse(File file) throws ParserUnfitException, ParserInconsistentException {

        // Every entry in this array resembles an object. In our case its a map/dictionary, which we will use for further DB-mapping
        ArrayList<Map<String, Object>> allObjects = new ArrayList<Map<String, String>>()

        // All lines in a file
        file.eachLine{ line ->
            // TODO: Optimize the "linesToIgnore"-process
            boolean skip = false
            for(String forbiddenLine in linesToIgnore){
                if(line.contains(forbiddenLine)){
                    // We found a line which the user told us to ignore => skip following procedure.
                    skip = true
                    break;
                }
            }

            if(!skip) {

                // Helpermaps -----------------------------
                // Map which represents a single object
                Map<String, Object> objectMap = [:]

                entries.each { entry_it ->
                    String entry;

                    // TODO implement optional
                    // Some files have lines with varying widths in the last column, that's why we sometimes have to truncate the width of the last column
                    if(entry_it.columnEnd - 1 > line.length())
                        entry = line.substring(entry_it.columnStart, line.length())
                    else
                        entry = line.substring(entry_it.columnStart, entry_it.columnEnd)

                    if(entry_it.trim)
                        entry = entry.trim();

                    def boolean isParseable = false
                    try {
                        isParseable = entry_it.checkType(entry)
                    }
                    catch (ParserUnfitException e) {
                        if(TokenSplitParser.checkIfStrictParsingNeeded(this.routines, line))
                            throw e
                    }

                    // Is this entry parseable? Yes? To the objectMap it goes.
                    if(isParseable)
                        objectMap.put(entry_it.field, parseField(entry_it.field, entry))
                    else
                        objectMap.put(entry_it.field, entry)
                }

                // Finished parsing a line, which resembles an object. Add to dat map.
                allObjects.add(objectMap)
            }
        }
        return allObjects
    }

    String toString() {
        "Columnwidthparser for " + name
    }


}
