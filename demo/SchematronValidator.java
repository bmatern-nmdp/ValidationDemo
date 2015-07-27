
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

    /**
     * Perform a schematron validation for an xml string against an array of schemaFileName strings.
     *
     * @param xml a String containing the xml to validate
     * @param schemaFileNames an array of Strings containing the names of the schema file resources to validate against
     * @return an array of ValidationError objects found during validation
     */
    public static boolean validate(String xml, String schemaFileName)
    {
        try
        {
            URL jarURL = SchematronValidator.class.getResource(jarFileName);
            URI jarURI = jarURL.toURI();
            loadedProbatronClasses = loadJarElements(new File(jarURI));
            
            //We're using some reflection here, so object types are vague
            Object vr = null;
            //theSchema = org.probatron.SchematronSchema
            Object theSchema = null;

            URL schemaFileURL = SchematronValidator.class.getResource(schemaFileName);
            InputStream xmlInputStream = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));            
           
            //A org.probatron.SchematronSchema object needs to have a Session object when it calls validateCandidate(), or else Null Pointers.
            //So I create a session object here to please it.
            Class sessionClass= loadedProbatronClasses.loadClass("org.probatron.Session");
            Object currentSession = sessionClass.newInstance();
            
            //Create a SchematronSchema object, using constructor that takes a Session and a schema URL
            Class schematronSchemaClass= loadedProbatronClasses.loadClass("org.probatron.SchematronSchema");
            Constructor ctor = schematronSchemaClass.getDeclaredConstructor(sessionClass, URL.class);
            theSchema = ctor.newInstance(currentSession, schemaFileURL);
            
            //Validate against a schematron schema, using probatron's validateCandidate method
            vr = callReflectedMethod(theSchema,"validateCandidate", xmlInputStream, Class.forName("java.io.InputStream"));


            Object validationReportObject = vr;
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
        return true;
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
            method.setAccessible(true);
            return method.invoke(callingObject, singleParameter);
        } 
        catch (Exception e)
        {
            System.out.println("Exception while calling reflected method: " + e);
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
            System.out.println("Error during schematron validation:" + e);
        }
        return null;
    }

    /**
     * Perform a schematron validation for an xml string against an single schematron schema.
     * This method mimics Probatron's Session.doValidation.
     * 
     * @param xml a String containing the xml to validate
     * @param schemaLocation an String containing the name of the schema file resource to validate against
     * @return an object which is an org.probatron.ValidationReport objects.
     */
    private static Object doValidation(String xml, String schemaLocation) 
    {
        //We're using some reflection here, so object types are vague
        //vr = org.probatron.ValidationReport
        Object vr = null;
        //theSchema = org.probatron.SchematronSchema
        Object theSchema = null;
        
        try 
        {
            URL schemaFileURL = SchematronValidator.class.getResource(schemaLocation);
            InputStream xmlInputStream = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));            
           
            //A org.probatron.SchematronSchema object needs to have a Session object when it calls validateCandidate(), or else Null Pointers.
            //So I create a session object here to please it.
            Class sessionClass= loadedProbatronClasses.loadClass("org.probatron.Session");
            Object currentSession = sessionClass.newInstance();
            
            //Create a SchematronSchema object, using constructor that takes a Session and a schema URL
            Class schematronSchemaClass= loadedProbatronClasses.loadClass("org.probatron.SchematronSchema");
            Constructor ctor = schematronSchemaClass.getDeclaredConstructor(sessionClass, URL.class);
            theSchema = ctor.newInstance(currentSession, schemaFileURL);
            
            //Validate against a schematron schema, using probatron's validateCandidate method
            vr = callReflectedMethod(theSchema,"validateCandidate", xmlInputStream, Class.forName("java.io.InputStream"));
        } 
        catch(Exception e)
        {
            System.out.println("Exception in doValidation: " + e);
        }

        return vr;
    }
}
