import javafx.util.Pair;
import sun.awt.Mutex;

import javax.annotation.processing.FilerException;
import java.io.*;
import java.util.Map;
import java.util.TreeMap;

public class Indexer {

    Map<String, String> termDictionary;
    Map<String, String> docDictionary;
    File directory;
    File subFolderTerms;
    File subFolderDocs;


    public Indexer(boolean stem) throws IOException {
        termDictionary = new TreeMap<>();
        docDictionary = new TreeMap<>();

        boolean corpus;
        boolean subFolder1;
        boolean subFolder2;
        if (stem) {

            directory = new File("./resources/StemmedCorpus");
            corpus = directory.mkdir();
            subFolderTerms = new File("./resources/StemmedCorpus/Terms");
            subFolder1 = subFolderTerms.mkdir();
            subFolderDocs = new File("./resources/StemmedCorpus/Docs");
            subFolder2= subFolderDocs.mkdir();

        } else {

            directory = new File("./resources/Corpus");
            corpus = directory.mkdir();
            subFolderTerms = new File("./resources/StemmedCorpus/Terms");
            subFolder1 = subFolderTerms.mkdir();
            subFolderDocs = new File("./resources/StemmedCorpus/Docs");
            subFolder2= subFolderDocs.mkdir();

        }

        if (!corpus || !subFolder1 || !subFolder2) {
            throw new IOException("cannot create directory for indexer corpus");
        }
    }

    public boolean addBlock(Parser p) throws IOException {
        Mutex mutex = new Mutex();
        mutex.lock();
        boolean createdFile;
        for (Token tkn : p.getTermMap().keySet()) {
            File file = new File(subFolderTerms.getPath() + "/" + tkn.getStr().hashCode() + ".txt");
            if (!file.exists()) {
                createdFile = file.createNewFile();
                if (!createdFile) {
                    throw new FilerException("cannot create file for indexer corpus");
                }

                termDictionary.put(tkn.getStr(), file.getPath());
            }
            FileWriter filewriter = new FileWriter(file.getAbsoluteFile(), true);
            BufferedWriter bw = new BufferedWriter(filewriter);
            PrintWriter writer = new PrintWriter(filewriter);
            for (Map.Entry<String, Integer> pair : p.getTermMap().get(tkn).entrySet()) {
                writer.println(pair.getKey() + ":" + pair.getValue());
            }
            writer.close();
        }
        for(String docID: p.getWordCounter().keySet()){
            File file = new File(subFolderDocs.getPath() + "/" + docID + ".txt");
            if (!file.exists()) {
                createdFile = file.createNewFile();
                if (!createdFile) {
                    throw new FilerException("cannot create file for indexer corpus");
                }

                docDictionary.put(docID, file.getPath());
            }

            FileWriter filewriter = new FileWriter(file.getAbsoluteFile(), true);
            BufferedWriter bw = new BufferedWriter(filewriter);
            PrintWriter writer = new PrintWriter(filewriter);
            writer.println(p.getMaxTf().get(docID) + "," + p.getWordCounter().get(docID));

            writer.close();
        }

        mutex.unlock();
        return false;
    }

}
