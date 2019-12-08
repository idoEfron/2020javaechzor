import java.io.IOException;
import java.text.ParseException;

public class main {
    public static void main(String[] args) throws IOException, ParseException {
        System.out.println("Enter the path to search");
        ReadFile read = new ReadFile();
        Parser p = new Parser(read,false);
    }
}
