import java.util.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
public class Parser {

    private Map<String, ArrayList<String>> termMap;
    private HashSet<String> stopwords;

    public Parser(ReadFile read) throws IOException {
        termMap = new HashMap<String, ArrayList<String>>();
        stopwords = new HashSet<String>();
        //add stopwords to hashset
        /*
        this part of the code is from https://howtodoinjava.com/java/io/read-file-from-resources-folder/
         */
        String fileName = "stopwords.txt";
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        File stopWordsFile = new File(classLoader.getResource(fileName).getFile());
        String stopContent = new String(Files.readAllBytes(stopWordsFile.toPath()));
        String stopLines[] = stopContent.split("\\r?\\n");
        stopwords.addAll(Arrays.asList(stopLines));
        System.out.println(stopwords.size());
        ///////////////
        parseDocs(read.allFile);
    }

    public void parseDocs (ArrayList<String> docList) {
        for (int i=0;i<docList.size();i++) {
            if(!docList.get(i).equals("\n")&&!docList.get(i).equals("\n\n\n")&&!docList.get(i).equals("\n\n\n\n")&&!docList.get(i).equals("\n\n")){
                String docId = docList.get(i);
                String result = docId.substring(docId.indexOf("<DOCNO>")+8 , docId.indexOf("</DOCNO>")-1);
                String txt =  docId.substring(docId.indexOf("<TEXT>")+7 , docId.indexOf("</TEXT>"));
                String[] tokens = txt.split("\\s+|\n");
            }
        }
    }
}