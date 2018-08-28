package extraction

import transformation.*


abstract class DynamicParser {

    String name
    String description                          // Description of what the format of a file has to look like, to be parseable
    String selectorName                         // String the Filename must contain to be parsed by this parsing
    AllowedFiletype selectorFileType            // Filetype the parsing accepts

    SortedSet<TransformationRoutine> routines = new TreeSet<TransformationRoutine>()
    SortedSet<TransformationRoutine> entries = new TreeSet<TransformationRoutine>()

    static hasMany = [entries: DynamicParserEntry, routines: TransformationRoutine]

    abstract ArrayList<Map<String, Object>> parse(File file) throws ParserUnfitException, ParserInconsistentException

    boolean appliesTo(File file){
        return file.name.contains(selectorFileType.toString()) && file.name.contains(selectorName)
    }

    static boolean checkIfStrictParsingNeeded(SortedSet<TransformationRoutine> routines, String line){
        boolean is_needed = true

        routines.each{ routine ->
            routine.procedures.each {
                it.notable_objects.each {
                    if (line.contains(it.value)) {
                        is_needed = false
                    }
                }
            }
        }
        return is_needed
    }
}
