package extraction

import groovy.io.FileType
import testing.*
import grails.util.Holders
import transformation.DatabaseSaveException
import transformation.TransformationRoutine
import transformation.TransformationService

import java.nio.file.FileSystemException

class FileParsingService {

	static def parseAllFilesInDirectory(File dir){

		//ArrayList<ArrayList<Map<String, Object>>> parseResults = []
		//ArrayList....Files
		//ArrayList<ArrayList ... file
		//ArrayList<ArrayList<Map ... whole parse-object

		Integer parsedFilesCounter = 0
		String resultMessage = ""
		Integer httpCode = 200

		// Count all files, but not directories
		File[] filesWithoutDirectories = dir.listFiles(new FileFilter() {
			@Override
			boolean accept(File f) {
				return !f.isDirectory()
			}
		})

		if(filesWithoutDirectories == null)
			return [400, "Specified directory-string returns zero files in query. Please recheck your input."]

		Integer fileCount = filesWithoutDirectories.size()

		dir.eachFile (FileType.FILES) { file ->
			try {
				DynamicParser parser = findCorrespondingParser(file)
				if(parser) {
					ArrayList<Map<String, Object>> result = parser.parse(file)
					TransformationService.transformAndLoadData(result, parser)
					parsedFilesCounter++

					File finished_dir = new File((file.absolutePath - file.name) + "parsed files")
					finished_dir.mkdir()
					if(!file.renameTo(new File(finished_dir, file.getName())))
						throw new FileSystemException("Could not move file '${file.getName()}'! Is another process accessing it? Is a file with the same name already in the parsed files folder?")
				}
				else {
					println "No parser found for file ${file.name}"
				}
			}
			catch(Exception e) {
				resultMessage = e.toString()
				e.printStackTrace()
				httpCode = 500
				return [httpCode, resultMessage]
			}
		}

		if(httpCode == 200)
			resultMessage = "${parsedFilesCounter} out of ${fileCount} files were parsed and transformed"

		println resultMessage

		return [httpCode, resultMessage]
	}

	static DynamicParser findCorrespondingParser(File file) throws ParserInconsistentException
	{
		DynamicParser parser = null

		DynamicParser.getAll().each {
			if (it.appliesTo(file))
				if(parser == null)
					parser = it
				else
					throw new ParserInconsistentException("Multiple parsers found for a file. Which one to use? Parsers are: ${it.name} and ${parser.name}")
		}

		return parser
	}

	/*static def mapToObjects(ArrayList<ArrayList<Map<String, String>>> parsedObjects){
	   *//* parsedObjects.each{ file_it ->
			file_it.each{ object_it ->
				object_it.each{ property_it ->
					def multiple = false

					// Regex to check is [X] is attached to a property, where X = a number
					if(property_it.key.matches("(.*)\\[(.*)\\]"))
					{
						// Remove the [X] from the propertyname
						def propertyName = property_it.key.replaceAll("\\[(.*)\\]", "")

						switch (GrailsClassUtils.getPropertyType(object.getClass(), propertyName)) {
							case Integer: ((Set)object[propertyName]).add(Integer.parseInt(property_it.value));
								break;

							case Float: ((Set)object[propertyName]).add(Float.parseFloat(property_it.value));
								break;

//                            case String: ((Set)object[propertyName])[0] = (property_it.value);
//                                break;
						}
					}
					else {
						switch (GrailsClassUtils.getPropertyType(object.getClass(), property_it.key)) {
							case Integer: object[property_it.key] = Integer.parseInt(property_it.value);
								break;

							case Float: object[property_it.key] = Float.parseFloat(property_it.value);
								break;

							case String: object[property_it.key] = property_it.value;
								break;
						}
					}

				}
			}
		}*//*

		def ArrayList objectList = new ArrayList()
		def grailsApplication = Holders.getGrailsApplication()
		grailsApplication.getArtefacts("SplitTokenTest")*.clazz

		parsedObjects.each { file_it ->
			file_it.each { object_it ->
				def object = TokenSplitTest.newInstance()
				object.properties = object_it
				objectList.add(object)
			}
		}

		return objectList
	}*/
}
