import java.io.IOException;

public class main {
    public static void main(String[] args) throws IOException {
        System.out.println("Enter the path to search");
        ReadFile read = new ReadFile();
        Parser p = new Parser(read);

        //tests for isNumber
        System.out.println(p.isNumber("10,123","shaull"));
    }
    }
