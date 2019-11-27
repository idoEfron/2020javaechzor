import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import org.tartarus.snowball.ext.porterStemmer;


public class main {
    public static void main(String[] args) throws IOException, ParseException {
        System.out.println("Enter the path to search");
        ReadFile read = new ReadFile();
        Parser p = new Parser(read);
    }
}
