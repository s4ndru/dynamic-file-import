package extraction

import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import org.w3c.dom.Document
import org.w3c.dom.NodeList
import org.w3c.dom.Node
import org.w3c.dom.Element

class SimpleXMLParser extends DynamicParser{

    String superTag = null
    String excelTag = null
    Integer startBuffer = 0
    Integer endBuffer = 0

    static constraints = {
        superTag nullable: false, blank: false
        excelTag nullable: true, blank: false/*, validator: { val, obj ->
            return (val == null && obj.startBuffer == 0 && obj.endBuffer == 0) || (val != null && obj.startBuffer > 0)
        }*/

        startBuffer min: 0
        endBuffer min: 0
    }

    @Override
    ArrayList<Map<String, Object>> parse(File file) throws ParserUnfitException, ParserInconsistentException {
        ArrayList<Map<String, Object>> allObjects = new ArrayList<Map<String, Object>>()
        Map<String, Object> objectMap = null

        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance()
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder()
            Document doc = dBuilder.parse(file)
            doc.getDocumentElement().normalize()

            NodeList nList = doc.getElementsByTagName(superTag)

            if(startBuffer >= nList.getLength() - endBuffer)
                throw new ParserUnfitException("The startBuffer and endBuffer-configuration for the file: '" + file.getName() + "' result in no objects being parsed and passed on!")

            for (int i = startBuffer; i < nList.getLength() - endBuffer; i++) {

                Node nNode = nList.item(i)

                if(nNode.getNodeType() == Node.ELEMENT_NODE) {

                    Element eElement = (Element) nNode
                    objectMap = [:]

                    entries.eachWithIndex{ entry_it, entry_index ->
                        //eElement.getAttribute("id")
                        if(excelTag)
                            // The order of the tags is important if it is an excel tag!
                            objectMap.put(entry_it.field, entry_it.parseField(eElement.getElementsByTagName(excelTag).item(entry_index).getTextContent()))
                        else
                            objectMap.put(entry_it.field, entry_it.parseField(eElement.getElementsByTagName(entry_it.field).item(0).getTextContent()))
                    }

                    allObjects.add(objectMap)
                }
            }
        // If an error occurs => parsing process stops. Files which were parsed, are moved, unsuccessful files after the exception-file, are not parsed.
        } catch (ParserInconsistentException e){
            throw e
        }
        catch (ParserUnfitException e){
            throw e
        }
        catch (Exception e) {
            throw e
        }

        return allObjects
    }

    String toString(){
        "XMLParser for " + name
    }
}
