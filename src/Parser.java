import java.util.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.regex.Pattern;

public class Parser {

    private Map<String, ArrayList<String>> termMap;
    private HashSet<String> stopwords;

    public Parser(ReadFile read) throws IOException {
        termMap = new HashMap<>();
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
            if(!docList.get(i).equals("\n")&&!docList.get(i).equals("\n\n\n")&&!docList.get(i).equals("\n\n\n\n")&&!docList.get(i).equals("\n\n")) {
                String docId = docList.get(i);
                String result = docId.substring(docId.indexOf("<DOCNO>") + 8, docId.indexOf("</DOCNO>") - 1);
                if (docId.contains("<TEXT>")&&docId.contains("</TEXT>")) {
                    String txt = docId.substring(docId.indexOf("<TEXT>") + 7, docId.indexOf("</TEXT>"));
                    String[] tokens = txt.split("\\s+|\n");
                    ArrayList<String> afterCleaning = new ArrayList<>();
                    for (int y = 0; y < tokens.length; y++) {
                        String token = tokens[y];
                        if (token.length() > 0 && checkChar(token.charAt(0)) == false) {
                            token = token.substring(1, token.length());
                        }
                        if (token.length() > 0 && checkChar(token.charAt(token.length() - 1)) == false) {
                            token = token.substring(0, token.length() - 1);
                        }
                    }
                }
            }
        }
    }

    private boolean checkChar(char charAt) {
        return ((charAt >= 65 && charAt <= 90) || (charAt >= 97 && charAt <= 122)||(charAt >= 48 && charAt <= 57));

    }


    public boolean isNumber(String str,String docID){
        if(Character.isDigit(str.charAt(0))){
            Pattern pattern = Pattern.compile("\\d+(,\\d+)*(\\.\\d+)?");
            if(str.matches("\\d+(,\\d+)*(\\.\\d+)?")) {
                str= str.replaceFirst(",",".");
                str = str.substring(str.indexOf('.'),str.indexOf('.')+3);
                str= str+"K";
                if(termMap.containsKey(str)){
                    termMap.get(str).add(docID);
                }
                else{
                    termMap.put(str,new ArrayList<String>());
                    termMap.get(str).add(docID);
                }
                return true;
            }
        }

        return false;
    }



}