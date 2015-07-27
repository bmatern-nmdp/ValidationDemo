import java.io.File;
import java.io.StringReader;
import java.net.URL;

import javax.xml.XMLConstants;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.validation.*;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;


/** 
 * SchemaValidator is a class used to validate an XML document against an XML Schema.
 * 
 * The validation is performed by an org.xml.sax parser.  The validation logic is handled by
 * the subclass, MiringValidationContentHandler.
*/
public class SchemaValidator
{

    /**
     * Validate xml against a schema
     *
     * @param xml a String containing the XML to validate
     * @param schemaFileName the file name of the schema to compare against
     * @return an array of ValidationError objects found during validation
     */
    public static void validate(String xml, String schemaFileName) 
    {
        try 
        {
            URL schemaURL = SchemaValidator.class.getResource(schemaFileName);

            File schemaFile = new File(schemaURL.toURI());
            Schema schema = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI).newSchema(schemaFile);

            final SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setNamespaceAware(true);
            factory.setSchema(schema);
            
            final SAXParser parser = factory.newSAXParser();
            final MiringValidationContentHandler handler = new MiringValidationContentHandler();

            //parser.parse is what does the actual "validation."  It parses the sample xml referring to the schema.
            //Errors are thrown by the handler, and we'll turn those into validation errors that are human readable.
            parser.parse(new InputSource(new StringReader(xml)), handler);
        }
        catch (Exception e)
        {
            System.out.println("Exception during schema validation: " + e);
        }
    }

    /** 
     * MiringValidationContentHandler is a subclass of SchemaValidator, which is responsible for handling 
     * parse exceptions, and performing Miring Specific logic for determining Miring Results.
     * 
     * The methods in this class are overrides of DefaultHandler, which I extend to provide validation logic.
     * 
     * The startElement and endElement methods are used to construct the SimpleXmlModel for the document.
     * 
     * Parser exceptions are interpreted and translated into MIRING ValidationResults.
    */
    private static class MiringValidationContentHandler extends DefaultHandler 
    {
        @Override
        public void warning(SAXParseException exception) throws SAXException 
        {
            System.out.println("Sax Parser Warning: " + exception.getMessage());
        }
    
        @Override
        public void error(SAXParseException exception) throws SAXException 
        {
            System.out.println("Sax Parser NonFatal Error: " + exception.getMessage());
        }
    
        @Override
        public void fatalError(SAXParseException exception) throws SAXException 
        {
            System.out.println("Sax Parser Fatal Error: " + exception.getMessage());
        }
    }
}