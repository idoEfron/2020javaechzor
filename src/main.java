import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.Scanner;

import org.tartarus.snowball.ext.porterStemmer;


public class main {

    public static void main(String[] args) throws IOException, ParseException {
        System.out.println("Enter the path to search");
        Parser p = new Parser(true);
        Scanner scanner = new Scanner(System.in);
        String folderPath = scanner.next();
        File[] files1 = null;
        File folder = new File(folderPath);
        if (folder.isDirectory()) {
            File[] listOfSubFolders = folder.listFiles();
            //System.out.println(listOfSubFolders.length);
            for (File SubFolder : listOfSubFolders) {
                if (SubFolder.isDirectory()) {
                    ReadFile read = new ReadFile(SubFolder,p);
                    Thread t = new Thread(read);
                    t.start();
                    //p.parseDocs(read.allFile,index);
                }
            }
        }
        //Indexer index = new Indexer(true);
    }
}
