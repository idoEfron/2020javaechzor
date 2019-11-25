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


        ///add stopwords to hashset
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
        ////call the parseDocs function
        parseDocs(read.allFile);
    }

    /**
     * this function is responsibly is to split the documents to tokens
     * @param docList
     */
    public void parseDocs(ArrayList<String> docList) {
        for (int i = 0; i < docList.size(); i++) {
            if (!docList.get(i).equals("\n") && !docList.get(i).equals("\n\n\n") && !docList.get(i).equals("\n\n\n\n") && !docList.get(i).equals("\n\n")) {
                String docId = docList.get(i);
                String result = docId.substring(docId.indexOf("<DOCNO>") + 8, docId.indexOf("</DOCNO>") - 1);
                if (docId.contains("<TEXT>") && docId.contains("</TEXT>")) {
                    String txt = docId.substring(docId.indexOf("<TEXT>") + 7, docId.indexOf("</TEXT>"));
                    String[] tokens = txt.split("\\s+|\n");
                    ArrayList<String> afterCleaning = new ArrayList<>();
                    for (int y = 0; y < tokens.length; y++) {
                        String currToken = tokens[y];
                        String token = "";
                        if (currToken.contains("/")) {
                            if (Character.isDigit(currToken.charAt(0)) == false ||
                                    Character.isDigit(currToken.charAt(currToken.length() - 1)) == false) {
                                String[] afterRemoving = currToken.split("/");
                                for (int j = 0; j < afterRemoving.length; j++) {
                                    token = cleanToken(afterRemoving[j]);
                                    if (token.length() > 0) {
                                        afterCleaning.add(token);
                                    }
                                }
                            }
                        }else{
                            token = cleanToken(tokens[y]);
                            if (token.length() > 0) {
                                afterCleaning.add(token);
                            }
                        }//bracket on the else
                    }//for on the tokens after splite
                }
            }
        }//bracket on the for on the doc list's
    }

    /**
     * this function is cleaning the token
     *
     * @param token
     * @return
     */
    protected String cleanToken(String token) {
        if (token.length() > 0 && checkChar(token.charAt(0)) == false) {
            token = token.substring(1, token.length());
        }
        if (token.length() > 0 && checkChar(token.charAt(token.length() - 1)) == false) {
            token = token.substring(0, token.length() - 1);
        }
        if (token.length() > 0 && (checkChar(token.charAt(0)) == false || checkChar(token.charAt(token.length() - 1)) == false)) {
            token = cleanToken(token);
        }
        return token;
    }


    /**
     * this function check if the char is a comma
     *
     * @param charAt
     * @return
     */
    private boolean checkChar(char charAt) {
        return ((charAt >= 65 && charAt <= 90) || (charAt >= 97 && charAt <= 122) ||
                (charAt >= 48 && charAt <= 57) || charAt == '$' || charAt == '%');

    }

    // *************change public to private**********8
    public boolean isNumber(String str,String docID){
        if(Character.isDigit(str.charAt(0))){
            Pattern pattern = Pattern.compile("\\d+(,\\d+)*(\\.\\d+)?");
            if(str.matches("\\d+(,\\d+)*(\\.\\d+)?")) {
                int counter =0;
                for(int i = 0; i<str.length(); i ++) {
                    char theChar = str.charAt(i);
                    if(Character.compare(theChar,',') == 0){
                        counter++;
                    }
                }
                if (counter >0){

                    boolean dot = false;
                    for(int i = str.indexOf(',')+3;i> str.indexOf(','); i--){
                        char theChar = str.charAt(i);
                        int num = Integer.parseInt(String.valueOf(theChar));
                        if(num >0){
                            dot = true;

                        }
                        else{
                            if(dot == false)
                               str = str.substring(0,i);
                        }

                    }
                    if(dot == true){
                        str= str.replaceFirst(",",".");
                    }
                    else{
                        str= str.replaceFirst(",","");
                    }

                    switch(counter){
                        case 1:
                            str = str+"K";
                            break;
                        case 2:
                            str = str+"M";
                            break;
                        case 3:
                            str = str+"B";
                            break;
                        default:
                            break;
                    }
                }


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


    public boolean defineCase(ArrayList<String> tokens, int index, String docID){
        String before ="";
        String current=tokens.get(index);;
        String after="";

        if(index >0){
            before = tokens.get(index-1);
        }
        if(index<tokens.size()){
            after = tokens.get(index+1);
        }

        // checks number cases

        if(after.equals("Thousand")){
            tokens.remove(index+1);
            if(termMap.containsKey(current+"K")){
                termMap.get(current).add(docID);
            }
            else{
                termMap.put(current+"K",new ArrayList<String>());
                termMap.get(current+"K").add(docID);
            }
            return true;
        }

        if(after.equals("Million")){
            tokens.remove(index+1);
            if(termMap.containsKey(current+"M")){
                termMap.get(current).add(docID);
            }
            else{
                termMap.put(current+"M",new ArrayList<String>());
                termMap.get(current+"M").add(docID);
            }
        }

        return false;
    }


}