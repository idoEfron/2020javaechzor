import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
        ExecutorService executor= Executors.newFixedThreadPool(4);


        long startTime = System.currentTimeMillis();

        if (folder.isDirectory()) {
            File[] listOfSubFolders = folder.listFiles();
            //System.out.println(listOfSubFolders.length);
            for (File SubFolder : listOfSubFolders) {
                if (SubFolder.isDirectory()) {
                    ReadFile read = new ReadFile(SubFolder,indexer,stemming);
                    executor.execute(new Thread(read));
                    //t.start();
                    //read.run();
                }
            }
            while (!executor.isTerminated()) {
            }
            executor.shutdown();
            long endTime = System.currentTimeMillis();
            long totalTime = endTime - startTime;
            System.out.print(totalTime + " , ");
        }
        //Indexer index = new Indexer(true);
    }
}
