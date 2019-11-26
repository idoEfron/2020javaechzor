import com.sun.xml.bind.v2.runtime.unmarshaller.XsiNilLoader;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.regex.Pattern;

public class Parser {

    private Map<String, ArrayList<String>> termMap;
    private HashSet<String> stopwords;
    private Map<String, Integer> entities;

    public Parser(ReadFile read) throws IOException, ParseException {
        termMap = new HashMap<>();
        stopwords = new HashSet<String>();
        Map<String, String> months = new HashMap<String, String>() {{
            put("January", "01");
            put("JANUARY", "01");
            put("February", "02");
            put("FEBRUARY", "02");
            put("March", "03");
            put("MARCH", "03");
            put("April", "04");
            put("APRIL", "04");
            put("May", "05");
            put("MAY", "05");
            put("June", "06");
            put("JUNE", "06");
            put("July", "07");
            put("JULY", "07");
            put("August", "08");
            put("AUGUST", "08");
            put("September", "09");
            put("SEPTEMBER", "09");
            put("October", "10");
            put("OCTOBER", "10");
            put("November", "11");
            put("NOVEMBER", "11");
            put("December", "12");
            put("DECEMBER", "12");
        }};

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
     *
     * @param docList
     */
    public void parseDocs(ArrayList<String> docList) throws ParseException {
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
                        } else {
                            token = cleanToken(tokens[y]);
                            if (token.length() > 0) {
                                afterCleaning.add(token);
                            }
                        }//bracket on the else
                    }//for on the tokens after splite
                    for (int j = 0; j < afterCleaning.size(); j++) {
                        if (!(numberHandler(afterCleaning, j, result))) {
                            stringHandler(afterCleaning, j, result);

                            /*if(termMap.containsKey(afterCleaning.get(j))){
                                termMap.get(afterCleaning.get(j)).add(result);
                            }
                            else{
                                termMap.put(afterCleaning.get(j),new ArrayList<String>());
                                termMap.get(afterCleaning.get(j)).add(result);
                            }*/
                        }
                        if(afterCleaning.get(j).contains("-")){
                            String[] strArray = afterCleaning.get(j).split("-");
                            ArrayList<String> rangeList = new ArrayList<String>();
                            rangeList.addAll(Arrays.asList(strArray));
                            //parseDocs(rangeList);

                        }
                    }
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

    // *************change public to private***********
    //checks if the input number is indeed a number
    public boolean isNumber(String str) throws ParseException {
        if (Character.isDigit(str.charAt(0))) {
            Pattern pattern = Pattern.compile("\\d+(,\\d+)*(\\.\\d+)?");
            if (str.matches("\\d+(,\\d+)*(\\.\\d+)?")) {

                return true;
            }
        }

        return false;
    }


    public boolean numberHandler(ArrayList<String> tokens, int index, String docID) throws ParseException {
        String before = "";
        String current = tokens.get(index);
        ;
        String after = "";
        String afterTwo = "";
        String afterThree = "";
        String num = current.replaceAll(",", "");

        if (index > 0) {
            before = tokens.get(index - 1);
        }
        if (index < tokens.size() - 1) {
            after = tokens.get(index + 1);
        }
        if (index < tokens.size() - 2) {
            afterTwo = tokens.get(index + 2);
        }
        if (index < tokens.size() - 3) {
            afterThree = tokens.get(index + 3);
        }

        // checks literal number cases

        if (isNumber(current) || current.contains("$") || current.contains("/")) {
            if (after.equals("Thousand")) {
                tokens.remove(index + 1);
                if (termMap.containsKey(current + "K")) {
                    if (!termMap.get(current + "K").contains(docID)) {
                        termMap.get(current + "K").add(docID);
                    }

                } else {
                    termMap.put(current + "K", new ArrayList<String>());
                    termMap.get(current + "K").add(docID);
                }
                return true;
            } else if (after.equals("Million")) {
                tokens.remove(index + 1);
                if (termMap.containsKey(current + "M")) {
                    if (!termMap.get(current + "M").contains(docID)) {
                        termMap.get(current + "M").add(docID);
                    }
                } else {
                    termMap.put(current + "M", new ArrayList<String>());
                    termMap.get(current + "M").add(docID);
                }
            } else if (after.equals("Billion")) {
                tokens.remove(index + 1);
                if (termMap.containsKey(current + "B")) {
                    if (!termMap.get(current + "B").contains(docID)) {
                        termMap.get(current + "B").add(docID);
                    }

                } else {
                    termMap.put(current + "B", new ArrayList<String>());
                    termMap.get(current + "B").add(docID);
                }
            }

            //checks if the case is percentage

            else if (after.equals("percent") || after.equals("percentage")) {
                tokens.remove(index + 1);
                if (termMap.containsKey(current + "%")) {
                    if (!termMap.get(current + "%").contains(docID)) {
                        termMap.get(current + "%").add(docID);
                    }

                } else {
                    termMap.put(current + "%", new ArrayList<String>());
                    termMap.get(current + "%").add(docID);
                }
            }
            //*******************dollars************************************************
            else if (after.equals("Dollars") || current.contains("$") || (after.equals("billion") && afterTwo.equals("U.S")
                    && afterThree.equals("dollars")) || (after.equals("million") && afterTwo.equals("U.S")) && afterThree.equals("dollars")) {
                if (current.contains("$")) {
                    current = current.substring(1);
                    String numDub = current.replaceAll(",", "");
                    if ((Double.parseDouble(numDub) < 1000000)&&(!after.equals("million")||!after.equals("billion"))) {
                        putTerm(current, " Dollars",docID);
                    }else if(after.equals("million")){
                        putTerm(current, " M Dollars",docID);
                    }else if(after.equals("billion")){
                        putTerm(current+"000", " M Dollars",docID);
                    }
                    else if(Double.parseDouble(numDub) >= 1000000){
                        NumberFormat format = NumberFormat.getInstance(Locale.FRANCE);
                        Number number = format.parse(current);
                        double d = number.doubleValue();
                        current = Double.toString(d);
                        putTerm(current, " M Dollars",docID);
                    }
                }////$$$$
                else if(after.equals("Dollars")){
                    if(current.contains("m")){
                        current = current.substring(0,current.length()-1);
                        putTerm(current, " M Dollars",docID);
                    }else if(current.contains("bn")){
                        putTerm(current.substring(0,current.length()-2)+"000", " M Dollars",docID);

                    }
                    String numDub = current.replaceAll(",", "");
                    if(Double.parseDouble(numDub) >= 1000000&&!current.contains("/")){
                        NumberFormat format = NumberFormat.getInstance(Locale.FRANCE);
                        Number number = format.parse(current);
                        double d = number.doubleValue();
                        current = Double.toString(d);
                        putTerm(current, " M Dollars",docID);
                    }
                    else if(Double.parseDouble(numDub) < 1000000 && !current.contains("/")){
                        putTerm(current, " Dollars",docID);
                    }else if(isNumber(before)&&current.contains("/")){
                        putTerm( before+" "+current, " Dollars",docID);
                    }
                }else if(isNumber(current)&&after.equals("billion")&&afterTwo.equals("U.S")&&afterThree.equals("dollars")){
                    putTerm(current+"000", " M Dollars",docID);
                }
                else if(isNumber(current)&&after.equals("million")&&afterTwo.equals("U.S")&&afterThree.equals("dollars")){
                    putTerm(current, " M Dollars",docID);
                }
            }

            ///********************************************************************************
            //regular number
            else if (Double.parseDouble(num) >= 1000) {
                int counter = 0;
                for (int i = 0; i < current.length(); i++) {
                    char theChar = current.charAt(i);
                    if (Character.compare(theChar, ',') == 0) {
                        counter++;
                    }
                }

                // handle case of number without additional word (such as thousand, million and etc..)
                if (counter > 0) {

                    NumberFormat format = NumberFormat.getInstance(Locale.FRANCE);
                    Number number = format.parse(current);
                    double d = number.doubleValue();
                    current = Double.toString(d);

                    switch (counter) {
                        case 1:
                            current = current + "K";
                            break;
                        case 2:
                            current = current + "M";
                            break;
                        case 3:
                            current = current + "B";
                            break;
                        default:
                            break;
                    }

                    if (termMap.containsKey(current)) {
                        if (!termMap.get(current).contains(docID)) {
                            termMap.get(current).add(docID);
                        }
                    } else {
                        termMap.put(current, new ArrayList<String>());
                        termMap.get(current).add(docID);
                    }
                }
            } else if (Double.parseDouble(current) < 1000) {
                if (termMap.containsKey(current)) {
                    if (!termMap.get(current).contains(docID)) {
                        termMap.get(current).add(docID);
                    }
                } else {
                    termMap.put(current, new ArrayList<String>());
                    termMap.get(current).add(docID);
                }
            }

        }
        return false;
    }

    private void putTerm(String current, String character, String docId) {
        if (termMap.containsKey(current + character)) {
            if (!termMap.get(current + character).contains(docId)) {
                termMap.get(current + character).add(docId);
            }

        } else {
            termMap.put(current + character, new ArrayList<String>());
            termMap.get(current + character).add(docId);
        }
    }

    public boolean stringHandler(ArrayList<String> tokens, int index, String docID) {

        String before = "";
        String current = tokens.get(index);
        ;
        String after = "";

        if (index > 0) {
            before = tokens.get(index - 1);
        }
        if (index < tokens.size() - 1) {
            after = tokens.get(index + 1);
        }

        if (!stopwords.contains(current.toLowerCase())) {
            if (Character.isUpperCase(current.charAt(0))) {
                if (termMap.containsKey(current.toLowerCase())) {
                    if ((!termMap.get(current.toLowerCase()).contains(docID))) {
                        termMap.get(current.toLowerCase()).add(docID);
                    }
                } else if (termMap.containsKey(current.toUpperCase())) {
                    if (!termMap.get(current.toUpperCase()).contains(docID)) {
                        termMap.get(current.toUpperCase()).add(docID);
                    }
                } else {
                    termMap.put(current.toUpperCase(), new ArrayList<String>());
                    termMap.get(current.toUpperCase()).add(docID);
                }
            } else if (Character.isLowerCase(current.charAt(0))) {
                if (termMap.containsKey(current.toUpperCase())) {
                    termMap.put(current.toLowerCase(), termMap.remove(current.toUpperCase())); // remove uppercase key and update to lowercase key
                    if (!termMap.get(current.toLowerCase()).contains(docID)) {
                        termMap.get(current.toLowerCase()).add(docID);
                    }
                }
            }
        }

        return false;
    }

    public boolean rangeHandler(ArrayList<String> tokens, int index, String docID){


        return false;
    }


    }