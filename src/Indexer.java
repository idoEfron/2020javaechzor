import com.google.protobuf.MapEntry;
import javafx.util.Pair;
import sun.awt.Mutex;

import javax.annotation.processing.FilerException;
import java.io.*;
import java.util.Map;
import java.util.TreeMap;

public class Indexer {

    Map<String, String> termDictionary;
    Map<String,String> docDictionary;
    File directory;
    String dirPath;


    public Indexer(boolean stem) throws IOException {
        termDictionary= new TreeMap<>();
        docDictionary = new TreeMap<>();
        boolean folder;
        if(stem){
            directory = new File("./resources/StemmedCorpus");
            folder = directory.mkdir();
        }
        else{
            directory = new File("./resources/Corpus");
            folder = directory.mkdir();
        }
        if(!folder){
            throw new IOException("cannot create directory for indexer corpus");
        }
    }

    public boolean addBlock(Parser p) throws IOException {
        Mutex mutex = new Mutex();
        mutex.lock();
        boolean createdFile;
        for(String str : p.getTermMap().keySet()){
            File file = new File(directory.getPath()+"/"+str+".txt");
            if(!file.exists()){
                createdFile= file.createNewFile();
                if(!createdFile){
                    throw new FilerException("cannot create file for indexer corpus");
                }
                termDictionary.put(str,file.getPath());

                FileWriter filewriter = new FileWriter(file.getAbsoluteFile(),true);
                BufferedWriter bw = new BufferedWriter(filewriter);
                PrintWriter writer = new PrintWriter(filewriter);
                for(Map.Entry<String,Integer> pair: p.getTermMap().get(str).entrySet()){
                    writer.println(pair.getKey() +":" +pair.getValue());
                }
                writer.close();
            }
            else{
                FileWriter filewriter = new FileWriter(file.getAbsoluteFile(),true);
                BufferedWriter bw = new BufferedWriter(filewriter);
                PrintWriter writer = new PrintWriter(filewriter);
                for(Map.Entry<String,Integer> pair: p.getTermMap().get(str).entrySet()){
                    writer.println(pair.getKey() +":" +pair.getValue());
                }
                writer.close();

            }

        }

        mutex.unlock();
        return false;
    }

}
