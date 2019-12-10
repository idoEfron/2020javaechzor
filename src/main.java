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
        Scanner scanner = new Scanner(System.in);
        String folderPath = scanner.next();
        File[] files1 = null;
        boolean stemming = true;
        File folder = new File(folderPath);
        Indexer indexer= new Indexer(true);
        if (folder.isDirectory()) {
            File[] listOfSubFolders = folder.listFiles();
            //System.out.println(listOfSubFolders.length);
            for (File SubFolder : listOfSubFolders) {
                if (SubFolder.isDirectory()) {
                    ReadFile read = new ReadFile(SubFolder,indexer,stemming);
                    Thread t = new Thread(read);
                    t.start();
                }
            }
        }
        //Indexer index = new Indexer(true);
    }
}
