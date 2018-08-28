package extraction

import groovy.io.FileType
import testing.*
import grails.util.Holders
import transformation.ConstraintException
import transformation.TransformationRoutine
import transformation.TransformationService

import java.nio.file.FileSystemException

class FileParsingService {

    static ArrayList<ArrayList<Map<String, Object>>> parseAllFilesInDirectory(File dir) throws ParserUnfitException, ParserInconsistentException, FileSystemException, ConstraintException {

        ArrayList<ArrayList<Map<String, Object>>> parseResults = []
        //ArrayList....Files
        //ArrayList<ArrayList ... dataLines
        //ArrayList<ArrayList<Map ... one dataLine
        Integer parsedFilesCounter = 0

        // Count all files, but not directories
        Integer fileCount = dir.listFiles(new FileFilter() {
            @Override
            boolean accept(File f) {
                return !f.isDirectory()
            }
        }).size()

        dir.eachFile (FileType.FILES) { file ->
            try {
                DynamicParser parser = findCorrespondingParser(file)
                if(parser) {
                    // TODO uhh.. rethink what to do here... halp Gernot??
                    ArrayList<Map<String, Object>> result = parser.parse(file)
                    File finished_dir = new File((file.absolutePath - file.name) + "parsed files")
                    finished_dir.mkdir()
                    if(!file.renameTo(new File(finished_dir, file.getName())))
                        throw new FileSystemException("Could not move file '" + file.getName() + "'! Is another process accessing it? Is a file with the same name already in the parsed files folder?")

                    TransformationService.transformAndLoadData(result, parser)
                    parseResults.add(result)
                    parsedFilesCounter++

                }
                else {
                    println "Whoops, no parser found for file ${file.name}"
                    return null
                }
            }
            catch(ParserUnfitException e) {
                e.printStackTrace()
            }
            catch(ParserInconsistentException e) {
                e.printStackTrace()
            }
            catch(ConstraintException e) {
                e.printStackTrace()
            }
            catch(Exception e) {
                e.printStackTrace()
            }
        }

        println "${parsedFilesCounter} out of ${fileCount} files were parsed and transformed"

        return parseResults
    }

    static DynamicParser findCorrespondingParser(File file) throws ParserInconsistentException
    {
        DynamicParser parser = null

        DynamicParser.getAll().each {
            if (it.appliesTo(file))
                if(parser == null)
                    parser = it
                else
                    throw new ParserInconsistentException("Multiple Parsers found for a file. Which one to use?")
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
