package extraction

import transformation.*


abstract class DynamicParser {

    String name
    String description                          // Description of what the format of a file has to look like, to be parseable
    // TODO maybe hasMany selectorName & selectorFileType
    String selectorName                         // String the Filename must contain to be parsed by this parsing
    AllowedFiletype selectorFileType            // Filetype the parsing accepts

    SortedSet<TransformationRoutine> routines = new TreeSet<TransformationRoutine>()
    SortedSet<TransformationRoutine> entries = new TreeSet<TransformationRoutine>()

    static hasMany = [entries: DynamicParserEntry, routines: TransformationRoutine]

    abstract ArrayList<Map<String, Object>> parse(File file) throws ParserUnfitException, ParserInconsistentException

    boolean appliesTo(File file){
        // TODO file.name.contains(selectorFileType) is too uncertain
        // TODO check at creation if combination is unique
        return file.name.contains(selectorFileType.toString()) && file.name.contains(selectorName)
    }
}
