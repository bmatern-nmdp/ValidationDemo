import java.io.File;
import java.io.StringReader;

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
    //Keep track of how many validation errors we find.
    static int errorCount = 0;
    
    // The main method is The starting point.
    public static void main(String[] args)
    {
        if(args.length == 2)
        {
            // args[0] = xml file name
            // args[0] = schema file name
            System.out.println("Attempting a Schema Validation of \"" + args[0] + "\" against schema \"" + args[1] + "\".\n");
            validate(args[0], args[1]);
        }
        else
        {
            System.out.println("The Schema Validator expects exactly 2 arguments: The XML Path+Name, and the Schema Path+Name");
        }
    }

    /**
     * Validate xml against a schema
     *
     * @param xml a String containing the filename of the XML to validate
     * @param schemaFileName a String containing the file name of the schema to compare against
     */
    public static void validate(String xmlFileName, String schemaFileName) 
    {
        try 
        {
            //Read our XML file into a string
            String xmlText = XMLReader.readFile(xmlFileName);
            //Schema is read into a javax.xml.validation.Schema object
            Schema schema = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI).newSchema(new File(schemaFileName));

            //Set up a SAXParser, which does the XML "validation"
            final SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setNamespaceAware(true);
            factory.setSchema(schema);
            final SAXParser parser = factory.newSAXParser();
            
            //The "handler" takes care of any problems encountered during validation.
            final MiringValidationContentHandler handler = new MiringValidationContentHandler();

            //parser.parse is what does the actual "validation."  It parses the sample xml referring to the schema.
            //Validation issues are handled by the handler.
            parser.parse(new InputSource(new StringReader(xmlText)), handler);
            
            System.out.println("Done Parsing the XML File.  " + errorCount + " total validation errors found.\n");
        }
        catch (Exception e)
        {
            System.out.println("Exception during schema validation:\n");
            e.printStackTrace();
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
            errorCount++;
            System.out.println("Sax Parser Warning: " + exception.getMessage() + "\n");
        }
    
        @Override
        public void error(SAXParseException exception) throws SAXException 
        {
            errorCount++;
            System.out.println("Sax Parser NonFatal Error: " + exception.getMessage() + "\n");
        }
    
        @Override
        public void fatalError(SAXParseException exception) throws SAXException 
        {
            errorCount++;
            System.out.println("Sax Parser Fatal Error: " + exception.getMessage() + "\n");
        }
    }
}