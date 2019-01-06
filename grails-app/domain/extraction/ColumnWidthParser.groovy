package extraction

import transformation.TransformationRoutine

class ColumnWidthParser extends DynamicParser{

    static hasMany = [linesToIgnore: String]

    static constraints = {
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
                    // We found a line which the user told us to ignore, or the line is empty => skip following procedure.
                    skip = true
                    break
                }
            }

            if(!skip) {

                // Helpermaps -----------------------------
                // Map which represents a single object
                Map<String, Object> objectMap = [:]
                boolean finishedLine = false

                entries.eachWithIndex { entry_it, entry_index ->
                    String value

                    if(finishedLine)
                        return

                    // Some files have lines with varying widths in the last column, that's why we sometimes have to truncate the width of the last column
                    if(entry_it.columnEnd - 1 >= line.length()) {

                        boolean isLastNonOptional = true

                        entry_index = entry_index + 1 > entries.size() ? entry_index + 1 : entry_index
                        entries.toList().subList(entry_index, entries.size()).each { check_entry_it ->
                            if (!check_entry_it.optional)
                                isLastNonOptional = false
                        }

                        if (!isLastNonOptional)
                            throw new ParserUnfitException("Cannot parse file, because a line ends before the end should be reached! I.e. when an entry is already at the end of a line but there are following non-optional entries specified for the parser.")
                        else {
                            value = line.substring(entry_it.columnStart - 1, line.length()).trim()
                        }

                        finishedLine = true
                    }
                    else
                        value = line.substring(entry_it.columnStart - 1, entry_it.columnEnd - 1).trim()

                    boolean isParseable = false
                    try {
                        isParseable = entry_it.checkType(value)
                    }
                    catch (ParserUnfitException e) {
                        if(checkIfStrictParsingNeeded(this.routines, value))
                            throw new ParserUnfitException(e.getMessage() + " (field called: '" + entry_it.field + "', value was: '" + value.toString() + "' )", e.cause)
                    }

                    // Is this entry parseable as specified? Yes? To the objectMap it goes. No? Means its optional! (else checkType() would have thrown an exception)
                    if(isParseable)
                        objectMap.put(entry_it.field, entry_it.parseField(value))
                    //else
                    //    throw new ParserInconsistentException("Field could not be parsed to specified type! Is the parser specified correctly? (entry was: '" + entry.toString() + "')")
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
