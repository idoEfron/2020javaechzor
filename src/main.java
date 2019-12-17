import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class main {

    public static void main(String[] args) throws IOException, ParseException {
        long startTime = System.currentTimeMillis();
        System.out.println("Enter the path to search");
        Scanner scanner = new Scanner(System.in);
        String folderPath = scanner.next();
        scanner.close();
        File[] files1 = null;
        boolean stemming = true;
        File folder = new File(folderPath);
        //Indexer indexer= new Indexer(true);
        ExecutorService executor= Executors.newFixedThreadPool(4);

        File subFolderTerms= null;
        boolean corpus;
        boolean subFolder1;
        boolean subFolder2;
        if (stemming) {

            File directory = new File("./StemmedCorpus");
            corpus = directory.mkdir();
            subFolderTerms = new File("./StemmedCorpus/Terms");
            subFolder1 = subFolderTerms.mkdir();
            File subFolderDocs = new File("./StemmedCorpus/Docs");
            subFolder2 = subFolderDocs.mkdir();
            for(char i = 'a' ; i<='z' ; i++){
                File Tfolder = new File("./StemmedCorpus/Terms/" +i);
                Tfolder.mkdir();
                File merged = new File(subFolderTerms.getPath()+"/"+i,i+"_merged.txt");
                merged.createNewFile();
            }
            File Sfolder = new File("./StemmedCorpus/Terms/special");
            Sfolder.mkdir();
            File merged = new File(subFolderTerms.getPath()+"/special","special"+"_merged.txt");
            merged.createNewFile();

            new File(subFolderDocs.getPath()+"/docDictionary").mkdir();
            File mergedDoc = new File(subFolderDocs.getPath()+"/docDictionary","docDictionary"+"_merged.txt");
            mergedDoc.createNewFile();
            new File(subFolderTerms.getPath()+"/termDictionary").mkdir();
            File mergedTerms = new File(subFolderTerms.getPath()+"/termDictionary","termDictionary"+"_merged.txt");
            mergedTerms.createNewFile();

        } else {

            File directory = new File("./Corpus");
            corpus = directory.mkdir();
            subFolderTerms = new File("./Corpus/Terms");
            subFolder1 = subFolderTerms.mkdir();
            File subFolderDocs = new File("./Corpus/Docs");
            subFolder2 = subFolderDocs.mkdir();
            for(char i = 'a' ; i<='z' ; i++){
                File Tfolder = new File("./Corpus/Terms/" +i);
                Tfolder.mkdir();
                File merged = new File(subFolderTerms.getPath()+"/"+i,i+"_merged.txt");
                merged.createNewFile();
            }
            File Sfolder = new File("./Corpus/Terms/special");
            Sfolder.mkdir();
            File merged = new File(subFolderTerms.getPath()+"/special","special"+"_merged.txt");
            merged.createNewFile();


            new File(subFolderDocs.getPath()+"/docDictionary").mkdir();
            File mergedDoc = new File(subFolderDocs.getPath()+"/docDictionary","docDictionary"+"_merged.txt");
            mergedDoc.createNewFile();
            new File(subFolderTerms.getPath()+"/termDictionary").mkdir();
            File mergedTerms = new File(subFolderDocs.getPath()+"/termDictionary","termDictionary"+"_merged.txt");
            mergedTerms.createNewFile();

        }

        if (!corpus || !subFolder1 || !subFolder2) {
            throw new IOException("cannot create directory for indexer corpus");
        }

        if (folder.isDirectory()) {
            File[] listOfSubFolders = folder.listFiles();
            //System.out.println(listOfSubFolders.length);
            List<File> files = new ArrayList<>();
            for (File SubFolder : listOfSubFolders) {
                if (SubFolder.isDirectory()) {
                    files.add(SubFolder);
                    if(files.size()==5){
                        ReadFile read = new ReadFile(new ArrayList<>(files),new Indexer(true),stemming);
                        executor.execute(new Thread(read));
                        files.clear();
                    }
                    //t.start();
                    //read.run();

                }

            }
            if(!files.isEmpty()){

            }
            executor.shutdown();
            while (!executor.isTerminated()) {
            }

        }

        executor= Executors.newFixedThreadPool(4);
        for (File file : subFolderTerms.listFiles()) {
            if (file.isDirectory()) {
                Merge merge = new Merge(file.listFiles());
                executor.execute(new Thread(merge));
                //merge.run();
            }
        }



        executor.shutdown();
        while (!executor.isTerminated()) {
        }

        long endTime = System.currentTimeMillis();
        long total = endTime-startTime;
        System.out.println("program took: "+total);
        //Indexer index = new Indexer(true);
    }
}
