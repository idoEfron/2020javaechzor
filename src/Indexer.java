import javafx.util.Pair;
import sun.awt.Mutex;

import javax.annotation.processing.FilerException;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;

public class Indexer {

    Map<String, String> termDictionary;
    Map<String, String> docDictionary;
    File directory;
    File subFolderTerms;
    File subFolderDocs;
    static Mutex mutex;
    int fileNum;


    public Indexer(boolean stem) throws IOException {
        termDictionary = new HashMap<>();
        docDictionary = new HashMap<>();

        if(!stem){
            subFolderTerms = new File("./Corpus/Terms");
            subFolderDocs= new File("./Corpus/Docs");
        }
        else{
            subFolderTerms = new File("./StemmedCorpus/Terms");
            subFolderDocs= new File("./StemmedCorpus/Docs");
        }


        fileNum = 0;
        mutex = new Mutex();

    }

    public boolean addBlock(Parser p) throws IOException {
        long startTime = System.currentTimeMillis();
        //mutex.lock();
        //System.out.println("indexing...");
        //Map<String, Map<String, Set>> addedLines = new HashMap<>();
        boolean createdFile;
        File file = null;
        File currentFile=null;
        FileWriter filewriter = null;
        BufferedWriter bw =null;
        PrintWriter writer=null;

        Map <Token,Map<String,Integer>> termMap = p.getTermMap();
        Set<Token> tknSet = termMap.keySet();


        long totalTime=0;

        Map <String ,List<String>> lines = new HashMap<>();

        for (Token tkn : tknSet) {
            //System.out.println(countLines(currentFile.getAbsolutePath()));
            if(Character.isLetter(tkn.getStr().charAt(0))){
                /*currentFile = new File(subFolderTerms.getPath()+"/"+tkn.getStr().toLowerCase().charAt(0)+"/"+tkn.getDocId()+".txt");
                if(!currentFile.exists()){
                    try{
                        currentFile.createNewFile();
                    }
                    catch(IOException e){
                        System.out.println(currentFile.getPath());
                    }
                }
                filewriter = new FileWriter(currentFile, true);
                bw = new BufferedWriter(filewriter);
                writer = new PrintWriter(bw);*/
                if(lines.containsKey(subFolderTerms.getPath()+"/"+tkn.getStr().toLowerCase().charAt(0)+"/"+tkn.getFile()+".txt")){
                    lines.get(subFolderTerms.getPath()+"/"+tkn.getStr().toLowerCase().charAt(0)+"/"+tkn.getFile()+".txt").add(tkn.getStr() +" : ");
                    Set <Map.Entry<String,Integer>> map = termMap.get(tkn).entrySet();
                    for (Map.Entry me : map){
                        lines.get(subFolderTerms.getPath()+"/"+tkn.getStr().toLowerCase().charAt(0)+"/"+tkn.getFile()+".txt").add(me.getKey()+"-" + me.getValue() +">> ");
                    }
                    lines.get(subFolderTerms.getPath()+"/"+tkn.getStr().toLowerCase().charAt(0)+"/"+tkn.getDocId()+".txt").add("\n");

                }
                else{
                    lines.put(subFolderTerms.getPath()+"/"+tkn.getStr().toLowerCase().charAt(0)+"/"+tkn.getFile()+".txt",new ArrayList<String>());
                    lines.get(subFolderTerms.getPath()+"/"+tkn.getStr().toLowerCase().charAt(0)+"/"+tkn.getFile()+".txt").add(tkn.getStr() +" : ");
                    Set <Map.Entry<String,Integer>> map = termMap.get(tkn).entrySet();
                    for (Map.Entry me : map){
                        lines.get(subFolderTerms.getPath()+"/"+tkn.getStr().toLowerCase().charAt(0)+"/"+tkn.getFile()+".txt").add(me.getKey()+"-" + me.getValue() +">> ");
                    }
                    lines.get(subFolderTerms.getPath()+"/"+tkn.getStr().toLowerCase().charAt(0)+"/"+tkn.getDocId()+".txt").add("\n");
                }

            }
            else{
                /*currentFile = new File(subFolderTerms.getPath()+"/"+"special/"+tkn.getDocId() +".txt");
                if(!currentFile.exists()){
                    currentFile.createNewFile();
                }
                termDictionary.put(tkn.getStr(),subFolderTerms.getPath()+"/"+"special/"+"special_merged.txt");
                filewriter = new FileWriter(currentFile, true);
                bw = new BufferedWriter(filewriter);
                writer = new PrintWriter(bw);*/
                if(lines.containsKey(subFolderTerms.getPath()+"/"+"special/"+tkn.getFile()+".txt")){
                    lines.get(subFolderTerms.getPath()+"/"+"special/"+tkn.getFile()+".txt").add(tkn.getStr() +" : ");
                    Set <Map.Entry<String,Integer>> map = termMap.get(tkn).entrySet();
                    for (Map.Entry me : map){
                        lines.get(subFolderTerms.getPath()+"/"+"special/"+tkn.getFile()+".txt").add(me.getKey()+"-" + me.getValue() +">> ");
                    }
                    lines.get(subFolderTerms.getPath()+"/"+"special/"+tkn.getDocId()+".txt").add("\n");
                }
                else{
                    lines.put(subFolderTerms.getPath()+"/"+"special/"+tkn.getFile()+".txt",new ArrayList<String>());
                    lines.get(subFolderTerms.getPath()+"/"+"special/"+tkn.getFile()+".txt").add(tkn.getStr() +" : ");
                    Set <Map.Entry<String,Integer>> map = termMap.get(tkn).entrySet();
                    for (Map.Entry me : map){
                        lines.get(subFolderTerms.getPath()+"/"+"special/"+tkn.getFile()+".txt").add(me.getKey()+"-" + me.getValue() +">> ");
                    }
                    lines.get(subFolderTerms.getPath()+"/"+"special/"+tkn.getDocId()+".txt").add("\n");
                }
            }
            if(!termDictionary.containsKey(tkn.getStr())){
                termDictionary.put(tkn.getStr(),subFolderTerms.getPath()+"/"+tkn.getStr().toLowerCase().charAt(0)+"/"+tkn.getStr().toLowerCase().charAt(0)+"_merged.txt");
            }
        }
        //writer.close();
        //bw.close();
        //filewriter.close();
        for (String str:lines.keySet()){
            writeRaw(lines.get(str),str);
        }

        /*long endTime = System.currentTimeMillis();
        totalTime = endTime - startTime;
        System.out.print("page indexed in: " + totalTime + " , ");*/

        for (String docID : p.getWordCounter().keySet()) {
            file = new File(subFolderDocs.getPath() + "/" + docID + ".txt");
            if (!file.exists()) {
                createdFile = file.createNewFile();
                if (!createdFile) {
                    throw new FilerException("cannot create file for indexer corpus" + docID);
                }

                docDictionary.put(docID, file.getPath());
            }
            filewriter = new FileWriter(file, true);
            bw = new BufferedWriter(filewriter);
            writer = new PrintWriter(bw);

            writer.print(p.getMaxTf().get(docID) + "," + p.getWordCounter().get(docID) + ">>");
            writer.close();
        }
        mutex.lock();
        //merge(subFolderTerms.listFiles());
        mutex.unlock();
        //mutex.unlock();

        long endTime = System.currentTimeMillis();
        totalTime = endTime - startTime;
        System.out.println("finished with file in:" +totalTime);

        return true;
    }

    //https://stackoverflow.com/questions/453018/number-of-lines-in-a-file-in-java

    public int countLines(String filename) throws IOException {
        InputStream is = new BufferedInputStream(new FileInputStream(filename));
        try {
            byte[] c = new byte[1024];

            int readChars = is.read(c);
            if (readChars == -1) {
                // bail out if nothing to read
                return 0;
            }

            // make it easy for the optimizer to tune this loop
            int count = 0;
            while (readChars == 1024) {
                for (int i = 0; i < 1024; ) {
                    if (c[i++] == '\n') {
                        ++count;
                    }
                }
                readChars = is.read(c);
            }

            // count remaining characters
            while (readChars != -1) {
                for (int i = 0; i < readChars; ++i) {
                    if (c[i] == '\n') {
                        ++count;
                    }
                }
                readChars = is.read(c);
            }

            int k = 0;
            return count;
        } finally {
            is.close();
        }
    }

    //https://stackoverflow.com/questions/5600422/method-to-find-string-inside-of-the-text-file-then-getting-the-following-lines/45168182
    public int getLineNum(String term, String path) {
        File file = new File(path);

        try {
            Scanner scanner = new Scanner(file);

            int lineNum = 0;
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                lineNum++;
                if (line.contains(term)) {
                    return lineNum;

                }
            }
        } catch (FileNotFoundException e) {
            return -1;
        }
        return -1;
    }

    public void addToLine(File file, int line, File folder) throws IOException {


        List<String> lines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
        Collections.sort(lines);
        Files.write(file.toPath(), lines, StandardCharsets.UTF_8);

    }

    //https://stackoverflow.com/questions/1062113/fastest-way-to-write-huge-data-in-text-file-java
    private static void writeRaw(List<String> records,String filePath) throws IOException {
        File file = new File( filePath );
        file.createNewFile();
        FileWriter writer = new FileWriter(file);
        write(records, writer);
    }

    private static void write(List<String> records, Writer writer) throws IOException {
        for (String record: records) {
            writer.write(record);
        }
        writer.flush();
        writer.close();
    }

}
