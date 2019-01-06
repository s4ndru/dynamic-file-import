package extraction

import transformation.*


abstract class DynamicParser {

	String name
	String description                          // Description of what the format of a file has to look like, to be parseable
	String selectorName                         // String the Filename must contain to be parsed by this parsing
	AllowedFileType selectorFileType            // Filetype the parsing accepts

	SortedSet<TransformationRoutine> routines = new TreeSet<TransformationRoutine>()
	SortedSet<DynamicParserEntry> entries = new TreeSet<DynamicParserEntry>()

	static hasMany = [entries: DynamicParserEntry, routines: TransformationRoutine]

	static constraints = {
		entries( validator: { entries ->
			entries.every() { entry -> entries.field.findAll{ entry.field == it }.size() == 1 }
		})
	}

	abstract ArrayList<Map<String, Object>> parse(File file) throws ParserUnfitException, ParserInconsistentException

	boolean appliesTo(File file){
		return file.name.contains(selectorFileType.toString()) && file.name.contains(selectorName)
	}

	static boolean checkIfStrictParsingNeeded(SortedSet<TransformationRoutine> routines, String line){
		boolean is_needed = true

		routines.each{ routine ->
			routine.procedures.each { procedure ->
				procedure.notable_objects.each {
					if (line.contains(it.value) && procedure.is_repetitive) {
						is_needed = false
					}
				}
			}
		}
		return is_needed
	}
}
