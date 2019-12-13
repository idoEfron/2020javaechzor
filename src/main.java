import java.io.File;
import java.io.IOException;
import java.text.ParseException;
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
        //Indexer indexer= new Indexer(true);
        ExecutorService executor= Executors.newFixedThreadPool(4);

        boolean corpus;
        boolean subFolder1;
        boolean subFolder2;
        if (stemming) {

            File directory = new File("./resources/StemmedCorpus");
            corpus = directory.mkdir();
            File subFolderTerms = new File("./resources/StemmedCorpus/Terms");
            subFolder1 = subFolderTerms.mkdir();
            File subFolderDocs = new File("./resources/StemmedCorpus/Docs");
            subFolder2 = subFolderDocs.mkdir();
            for(char i = 'a' ; i<='z' ; i++){
                File Tfolder = new File("./resources/StemmedCorpus/Terms/" +i);
                Tfolder.mkdir();
                File merged = new File(folder.getPath(),i+"_merged.txt");
                merged.createNewFile();
            }
            File Sfolder = new File("./resources/StemmedCorpus/Terms/special");
            Sfolder.mkdir();
            File merged = new File(folder.getPath(),"special"+"_merged.txt");
            merged.createNewFile();

        } else {

            File directory = new File("./resources/Corpus");
            corpus = directory.mkdir();
            File subFolderTerms = new File("./resources/Corpus/Terms");
            subFolder1 = subFolderTerms.mkdir();
            File subFolderDocs = new File("./resources/Corpus/Docs");
            subFolder2 = subFolderDocs.mkdir();
            for(char i = 'a' ; i<='z' ; i++){
                File Tfolder = new File("./resources/Corpus/Terms/" +i);
                Tfolder.mkdir();
                File merged = new File(folder.getPath(),i+"_merged.txt");
                merged.createNewFile();
            }
            File Sfolder = new File("./resources/Corpus/Terms/special");
            Sfolder.mkdir();
            File merged = new File(folder.getPath(),"special"+"_merged.txt");
            merged.createNewFile();

        }

        if (!corpus || !subFolder1 || !subFolder2) {
            throw new IOException("cannot create directory for indexer corpus");
        }

        long startTime = System.currentTimeMillis();

        if (folder.isDirectory()) {
            File[] listOfSubFolders = folder.listFiles();
            //System.out.println(listOfSubFolders.length);
            for (File SubFolder : listOfSubFolders) {
                if (SubFolder.isDirectory()) {
                    ReadFile read = new ReadFile(SubFolder,new Indexer(true),stemming);
                    executor.execute(new Thread(read));
                    //t.start();
                    //read.run();
                }
            }
            executor.shutdown();
            while (!executor.isTerminated()) {
            }
            long endTime = System.currentTimeMillis();
            long totalTime = endTime - startTime;
            System.out.print(totalTime + " , ");
        }
        scanner.close();
        //Indexer index = new Indexer(true);
    }
}
