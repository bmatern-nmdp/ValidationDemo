import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.util.jar.JarFile;

/** 
 * SchematronValidator is a class used to validate an XML document against a set of schematron rules.  The schematron logic is handled, in this case, by Probatron.
 * 
 * Probatron is distributed as an executable jar, but it is rather inconvenient to write XML files to hard drive in order to use them.
 * I'm using reflection to call Probatron's methods in a few spots, rather than using their main method as a starting point.
 * 
 * For reference, see Probatron's documentation:
 * http://www.probatron.org/probatron4j.html
 * https://code.google.com/p/probatron4j/source/browse/#svn/trunk/
 * 
 * The source code is available at google right now, but I'm not sure how long it will be available since Google Code is ending...
 * 
*/
public class SchematronValidator
{
    static ClassLoader loadedProbatronClasses;
    static String jarFileName = "/jar/probatron.jar";

    // The main method is The starting point.
    public static void main(String[] args)
    {
        if(args.length == 2)
        {
            String xmlFileName = args[0];
            String schematronFileName = args[1];
            System.out.println("Attempting a Schematron Validation of \"" + xmlFileName 
                + "\" against schema \"" + schematronFileName + "\".\n");
            validate(XMLReader.readFile(xmlFileName), schematronFileName);
        }
        else
        {
            System.out.println("The Schematron Validator expects exactly 2 arguments: The XML Path+Name, and the Schema Path+Name");
            
            //String xmlFileName = "hml/Element4.CSB.bad.attributes.xml";
            //String schematronFileName = "schematron/MiringAll.sch";
            //validate(XMLReader.readFile(xmlFileName), schematronFileName);
        }
    }

    /**
     * Perform a schematron validation for an xml string against an array of schemaFileName strings.
     *
     * @param xmlText a String containing the xml to validate
     * @param schemaFileNames an array of Strings containing the names of the schema file resources to validate against
     * @return an array of ValidationError objects found during validation
     */
    public static void validate(String xmlText, String schemaFileName)
    {
        try
        {
            URL jarURL = SchematronValidator.class.getResource(jarFileName);
            URI jarURI = jarURL.toURI();
            //System.out.println("jarURI: " + jarURI);
            loadedProbatronClasses = loadJarElements(new File(jarURI));
            //System.out.println("loadedProbatronClasses: " + loadedProbatronClasses);
            
            //We're using some reflection here, so object types are vague
            Object validationResultObj = null;
            //theSchema = org.probatron.SchematronSchema
            Object schemaObj = null;

            URL schemaFileURL = SchematronValidator.class.getResource(schemaFileName);
            //System.out.println("schemaFileURL: " + schemaFileURL);
            InputStream xmlInputStream = new ByteArrayInputStream(xmlText.getBytes(StandardCharsets.UTF_8));
            //System.out.println("xmlInputStream: " + xmlInputStream);         
           
            //A org.probatron.SchematronSchema object needs to have a Session object when it calls validateCandidate(), or else Null Pointers.
            //So I create a session object here to please it.
            //It's not that important.
            Class sessionClass= loadedProbatronClasses.loadClass("org.probatron.Session");
            Object currentSession = sessionClass.newInstance();
            
            //Create a SchematronSchema object, using constructor that takes a Session and a schema URL
            Class schematronSchemaClass= loadedProbatronClasses.loadClass("org.probatron.SchematronSchema");
            Constructor ctor = schematronSchemaClass.getDeclaredConstructor(sessionClass, URL.class);
            schemaObj = ctor.newInstance(currentSession, schemaFileURL);
            
            //Validate against a schematron schema, using probatron's validateCandidate method
            validationResultObj = callReflectedMethod(schemaObj,"validateCandidate", xmlInputStream, Class.forName("java.io.InputStream"));

            Object validationReportObject = validationResultObj;
            //Object validationReportObject = doValidation(xml, schemaFileName);

            //Stream out the schematron report to a String
            ByteArrayOutputStream myBaos = new ByteArrayOutputStream();
            callReflectedMethod(validationReportObject, "streamOut", myBaos, Class.forName("java.io.OutputStream"));
            String resultString = myBaos.toString();
            
            System.out.println("Schematron Validation Results:\n" + resultString);
        }
        catch(Exception e )
        {
            System.out.println("Exception in validate: " + e);
        }
    }

    /**
     * Call a reflected method within a class.  This method must accept a single parameter
     *
     * @param callingObject The object which calls the method
     * @param methodName a String with the name of the reflected method
     * @param singleParameter The object parameter to pass into the reflected method.
     * @param parameterClass The class of the object expected by the reflected method.  It must be the correct class, and not an inherited class.
     * @return an Object which was returned by the reflected method.
     */
    public static Object callReflectedMethod(Object callingObject, String methodName, Object singleParameter, Class<?> parameterClass)
    {
        Method method = null;
        try 
        {
            method = callingObject.getClass().getDeclaredMethod(methodName, parameterClass);
            System.out.println("method: " + method);
            System.out.println("callingObject: " + callingObject);
            System.out.println("singleParameter: " + singleParameter);
            method.setAccessible(true);
            System.out.println("method: " + method);
            Object results = method.invoke(callingObject, singleParameter);
            System.out.println("results: " + results);
            return results;
        } 
        catch (Exception e)
        {
            System.out.println("Exception while calling reflected method: " + e);
            e.printStackTrace();
        }

        return null;
    }
    
    /**
     * Load Probatron Classes.  This method will crack open the jar file and make it's methods and objects accessible. 
     *
     * @param jarFileLocation The relative location of the jar file resource
     * @return a URLClassLoader object, which you can use to call probatron methods reflectively.
     */
    public static URLClassLoader loadJarElements(File jarFileLocation)
    {
        try
        {
            JarFile jarFile = new JarFile(jarFileLocation);

            URL[] urls = { new URL("jar:file:" + jarFileLocation +"!/") };
            URLClassLoader cl = URLClassLoader.newInstance(urls);

            jarFile.close();
            return cl;
        }
        catch(Exception e)
        {
            System.out.println("Error during schematron validation:\n");
            e.printStackTrace();
        }
        return null;
    }
}
