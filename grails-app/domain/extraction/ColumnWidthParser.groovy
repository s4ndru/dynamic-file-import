package extraction

import transformation.TransformationRoutine

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

                entries.eachWithIndex { entry_it, entry_index ->
                    String entry

                    // Some files have lines with varying widths in the last column, that's why we sometimes have to truncate the width of the last column
                    if(entry_it.columnEnd - 1 >= line.length()) {

                        // TODO: test dis last object which is non-optional
                        boolean isLastNonOptional = true

                        entries.subSet(entries.toArray()[entry_index] as TransformationRoutine, false, entries.last(), true).each { check_entry_it ->
                            if (check_entry_it.optional == false)
                                isLastNonOptional = false
                        }

                        if (!isLastNonOptional)
                            throw new ParserUnfitException("Cannot parse file, because a line ends before the end should be reached! E.g. when an entry is already at the end of a line but there are following non-optional entries specified for the parser.")
                        else
                            entry = line.substring(entry_it.columnStart - 1, line.length()).trim()
                    }
                    else
                        entry = line.substring(entry_it.columnStart - 1, entry_it.columnEnd - 1).trim()

                    boolean isParseable = false
                    try {
                        isParseable = entry_it.checkType(entry)
                    }
                    catch (ParserUnfitException e) {
                        if(checkIfStrictParsingNeeded(this.routines, line))
                            throw e
                    }

                    // Is this entry parseable as specified? Yes? To the objectMap it goes. No? Exception!
                    if(isParseable)
                        objectMap.put(entry_it.field, entry_it.parseField(entry))
                    else
                        throw new ParserInconsistentException("Field could not be parsed to specified type! Is the parser specified correctly?")
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
