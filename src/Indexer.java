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
        mutex.lock();
        boolean createdFile;
        File file =null;
        for (Token tkn : p.getTermMap().keySet()) {
            try{
                file = new File(subFolderTerms.getPath() ,tkn.getStr() + ".txt");
                if (!file.exists()) {
                    createdFile = file.createNewFile();
                    termDictionary.put(tkn.getStr(), file.getPath());
                }
            } catch (FilerException fe){
                throw new FilerException("cannot create file for indexer corpus: " +tkn.getStr());
            } catch(IOException e){
                System.out.println(tkn.getStr() +" from " +tkn.getDocId() +" failed");
            }

            PrintWriter writer =null;
            try{
                FileWriter filewriter = new FileWriter(file.getAbsoluteFile(), true);
                BufferedWriter bw = new BufferedWriter(filewriter);
                writer = new PrintWriter(filewriter);
                for (Map.Entry<String, Integer> pair : p.getTermMap().get(tkn).entrySet()) {
                    writer.println(pair.getKey() + ":" + pair.getValue());
                }
                writer.close();
            }
            catch (FileNotFoundException e){
                throw new FileNotFoundException("file not found: " +tkn.getStr() +" from doc: " +tkn.getDocId());
            }
        }
        for(String docID: p.getWordCounter().keySet()){
            file = new File(subFolderDocs.getPath() + "/" + docID + ".txt");
            if (!file.exists()) {
                createdFile = file.createNewFile();
                if (!createdFile) {
                    throw new FilerException("cannot create file for indexer corpus" +docID);
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
