import javafx.util.Pair;
import sun.awt.Mutex;

import javax.annotation.processing.FilerException;
import java.io.*;
import java.util.*;

public class Indexer {

    Map<String, String> termDictionary;
    Map<String, String> docDictionary;
    File directory;
    File subFolderTerms;
    File subFolderDocs;
    Mutex mutex;


    public Indexer(boolean stem) throws IOException {
        termDictionary = new TreeMap<>();
        docDictionary = new TreeMap<>();

        mutex = new Mutex();

        boolean corpus;
        boolean subFolder1;
        boolean subFolder2;
        if (stem) {

            directory = new File("./resources/StemmedCorpus");
            corpus = directory.mkdir();
            subFolderTerms = new File("./resources/StemmedCorpus/Terms");
            subFolder1 = subFolderTerms.mkdir();
            subFolderDocs = new File("./resources/StemmedCorpus/Docs");
            subFolder2 = subFolderDocs.mkdir();

        } else {

            directory = new File("./resources/Corpus");
            corpus = directory.mkdir();
            subFolderTerms = new File("./resources/StemmedCorpus/Terms");
            subFolder1 = subFolderTerms.mkdir();
            subFolderDocs = new File("./resources/StemmedCorpus/Docs");
            subFolder2 = subFolderDocs.mkdir();

        }

        if (!corpus || !subFolder1 || !subFolder2) {
            throw new IOException("cannot create directory for indexer corpus");
        }
    }

    public boolean addBlock(Parser p) throws IOException {
        mutex.lock();
        System.out.println("indexing...");
        boolean createdFile;
        File file = null;
        Set<Token> tknSet = p.getTermMap().keySet();
        long startTime = System.currentTimeMillis();
        File currentFile = new File("./resources/StemmedCorpus/Terms/","terms.txt");
        currentFile.createNewFile();
        for (Token tkn : tknSet) {
            /*if (new File(currentFolder.getPath()).list().length == 200) {
                folder++;
                currentFolder = new File("./resources/StemmedCorpus/Terms/" + folder);
                currentFolder.mkdir();
            }*/
            PrintWriter writer = null;
            if (!termDictionary.containsKey(tkn.getStr())) {
                //file = new File(subFolderTerms.getPath(), tkn.getStr().hashCode() + ".txt");
                //createdFile = file.createNewFile();
                termDictionary.put(tkn.getStr(), currentFile.getPath());
                FileWriter filewriter = new FileWriter(termDictionary.get(tkn.getStr()), true);
                BufferedWriter bw = new BufferedWriter(filewriter);
                writer = new PrintWriter(filewriter);
                writer.print(tkn.getStr() +": ");
            }
            /*try {

            } catch (FilerException fe) {
                throw new FilerException("cannot create file for indexer corpus: " + tkn.getStr());
            } catch (IOException e) {
                System.out.println(tkn.getStr() + " from " + tkn.getDocId() + " failed");
            }*/

            try {
                FileWriter filewriter = new FileWriter(termDictionary.get(tkn.getStr()), true);
                BufferedWriter bw = new BufferedWriter(filewriter);
                writer = new PrintWriter(filewriter);
                Set<Map.Entry<String, Integer>> map = p.getTermMap().get(tkn).entrySet();
                for (Map.Entry<String, Integer> pair : map) {
                    writer.print(pair.getKey() + ":" + pair.getValue() + " >> ");
                }
                writer.close();


            } catch (FileNotFoundException e) {
                throw new FileNotFoundException("file not found: " + tkn.getStr() + " from doc: " + tkn.getDocId() + ">>");
            }
        }

        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.print(totalTime + " , ");

        for (String docID : p.getWordCounter().keySet()) {
            file = new File(subFolderDocs.getPath() + "/" + docID + ".txt");
            if (!file.exists()) {
                createdFile = file.createNewFile();
                if (!createdFile) {
                    throw new FilerException("cannot create file for indexer corpus" + docID);
                }

                docDictionary.put(docID, file.getPath());
            }

            FileWriter filewriter = new FileWriter(file.getAbsoluteFile(), true);
            BufferedWriter bw = new BufferedWriter(filewriter);
            PrintWriter writer = new PrintWriter(filewriter);
            writer.print(p.getMaxTf().get(docID) + "," + p.getWordCounter().get(docID) + ">>");

            writer.close();
        }

        mutex.unlock();

        return false;
    }

}
