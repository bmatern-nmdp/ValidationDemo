import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class XMLReader
{
    public static String readFile(String fileName)
    {
        try
        {
            File fileURL = new File(fileName);
            BufferedReader xmlReader = new BufferedReader(new FileReader(fileURL));
            
            StringBuilder xmlBuffer = new StringBuilder();
            String line = xmlReader.readLine();
    
            while (line != null) 
            {
                xmlBuffer.append(line);
                xmlBuffer.append(System.lineSeparator());
                line = xmlReader.readLine();
            }
            xmlReader.close();
            
            String xmlText = xmlBuffer.toString();
            return xmlText;
        }
        catch(Exception e)
        {
            System.out.println("Problem reading the file at: " + fileName);
            return null;
        }
    }
}
