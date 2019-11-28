
import org.tartarus.snowball.ext.porterStemmer;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.regex.Pattern;

public class Parser {

    private Map<String, Map<String, Integer>> termMap;
    private HashSet<String> stopwords;
    private Map<String, Integer> entities;
    private Map<String, String> months;
    private Map<String, String> mass;
    private Map<String, String> electrical;
    private boolean stemming;
    private Map<String, Integer> maxTf;
    private Map<String,Integer> wordCounter;
    private List<String> termsInDoc;

    public Parser(ReadFile read,boolean stem) throws IOException, ParseException {
        wordCounter = new HashMap<>();
        termsInDoc = new ArrayList<>();
        stemming = stem;
        maxTf = new HashMap<>();
        termMap = new HashMap<>();
        stopwords = new HashSet<String>();
        months = new HashMap<String, String>() {{
            put("January", "01");
            put("JANUARY", "01");
            put("JAN", "01");
            put("Jan", "01");
            put("February", "02");
            put("FEBRUARY", "02");
            put("Feb", "02");
            put("FEB", "02");
            put("March", "03");
            put("MARCH", "03");
            put("MAR", "03");
            put("Mar", "03");
            put("April", "04");
            put("APRIL", "04");
            put("APR", "04");
            put("Apr", "04");
            put("May", "05");
            put("MAY", "05");
            put("June", "06");
            put("JUNE", "06");
            put("JUN", "06");
            put("Jun", "06");
            put("July", "07");
            put("JULY", "07");
            put("JUL", "07");
            put("Jul", "07");
            put("August", "08");
            put("AUGUST", "08");
            put("AUG", "08");
            put("Aug", "08");
            put("September", "09");
            put("SEPTEMBER", "09");
            put("Sep", "09");
            put("SEP", "09");
            put("October", "10");
            put("OCTOBER", "10");
            put("OCT", "10");
            put("Oct", "10");
            put("November", "11");
            put("NOVEMBER", "11");
            put("NOV", "11");
            put("Nov", "11");
            put("December", "12");
            put("DECEMBER", "12");
            put("DEC", "12");
            put("Dec", "12");
        }};
        mass = new HashMap<String, String>() {{
            put("kilogram", "KG");
            put("kilograms", "KG");
            put("kg", "KG");
            put("KG", "KG");
            put("Kilogram", "KG");
            put("Kilograms", "KG");
            put("grams", "G");
            put("gram", "G");
            put("Grams", "G");
            put("Gram", "G");
            put("milligram", "MG");
            put("Milligram", "MG");
            put("milligrams", "MG");
            put("Milligrams", "MG");
            put("milligram", "MG");
            put("Milligram", "MG");
            put("milligrams", "MG");
            put("Milligrams", "MG");
            put("ton", "T");
            put("Tons", "T");
            put("Ton", "T");
            put("tons", "T");

        }};

        electrical = new HashMap<String, String>() {{
            put("milliampere", "mA");
            put("Milliampere", "mA");
            put("milli-ampere", "mA");
            put("Milli-ampere", "mA");
            put("milliampere".toUpperCase(), "mA");
            put("Milli-ampere".toUpperCase(), "mA");
            put("Watt", "W");
            put("watt", "W");
            put("watt".toUpperCase(), "W");
            put("volt", "V");
            put("Volt", "V");
            put("VOlT", "V");
            put("kg", "KG");
            put("KG", "KG");
            put("Kilogram", "KG");
            put("kilograms", "KG");
            put("Kilogram".toUpperCase(), "KG");
            put("megawatt", "MW");
            put("Megawatt", "MW");
            put("Megawatt".toUpperCase(), "MW");
            put("Mega-watt", "MW");
            put("mega-watt", "MW");
            put("Mega-watt".toUpperCase(), "MW");
            put("kilowatt", "MW");
            put("Kilowatt", "MW");
            put("kilowatt".toUpperCase(), "MW");
            put("Kilo-watt", "MW");
            put("kilo-watt", "MW");
            put("kilo-watt".toUpperCase(), "MW");

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
        String result ="";
        for (int i = 0; i < docList.size(); i++) {
            if (!docList.get(i).equals("\n") && !docList.get(i).equals("\n\n\n") && !docList.get(i).equals("\n\n\n\n") && !docList.get(i).equals("\n\n")) {
                String docId = docList.get(i);
                result = docId.substring(docId.indexOf("<DOCNO>") + 8, docId.indexOf("</DOCNO>") - 1);
                if (docId.contains("<TEXT>") && docId.contains("</TEXT>")) {
                    String txt = docId.substring(docId.indexOf("<TEXT>") + 7, docId.indexOf("</TEXT>"));
                    String[] tokens = txt.split("\\s+|\n");
                    ArrayList<String> afterCleaning = new ArrayList<>();
                    for (int y = 0; y < tokens.length; y++) {
                        String currToken = tokens[y];
                        String token = "";
                        if (currToken.contains("/") &&
                                (Character.isDigit(currToken.charAt(0)) == false ||
                                        Character.isDigit(currToken.charAt(currToken.length() - 1)) == false)) {
                            String[] afterRemoving = currToken.split("/");
                            for (int j = 0; j < afterRemoving.length; j++) {
                                token = cleanToken(afterRemoving[j]);
                                if (token.length() > 0) {
                                    afterCleaning.add(token);
                                }
                            }

                        } else {
                            token = cleanToken(tokens[y]);
                            if (token.length() > 0) {
                                afterCleaning.add(token);
                            }
                        }//bracket on the else
                    }//for on the tokens after split

                    handler(afterCleaning, result);
                }
            }
            wordCounter.put(result,termsInDoc.size());
            int k = 0;
        }//bracket on the for on the doc list's
    }

    private void handler(ArrayList<String> terms, String docID) throws ParseException {
        for (int i = 0; i < terms.size(); i++) {
            if (!(numberHandler(terms, i, docID))) {
                stringHandler(terms, i, docID);
                            /*if(termMap.containsKey(afterCleaning.get(j))){
                                termMap.get(afterCleaning.get(j)).add(result);
                            }
                            else{
                                termMap.put(afterCleaning.get(j),new ArrayList<String>());
                                termMap.get(afterCleaning.get(j)).add(result);
                            }*/
            }
            if (terms.get(i).contains("-")) {
                termMap.put(terms.get(i), new HashMap<String, Integer>());
                termMap.get(terms.get(i)).put(docID, 1);
                String[] strArray = terms.get(i).split("-");
                ArrayList<String> rangeList = new ArrayList<String>();
                rangeList.addAll(Arrays.asList(strArray));
                for (int j = 0; j < rangeList.size(); j++) {
                    if (rangeList.get(j).equals("")) {
                        rangeList.remove(j);
                    }
                }
                handler(rangeList, docID);

            }
        }
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
        if (token.length() == 1 && (token.equals("%") || token.equals("$"))) {
            token = "";
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

        if (isNumber(current) || current.contains("$") || current.contains("/") || current.charAt(current.length() - 1) == 'm' ||
                (current.contains("bn") && after.equals("Dollars"))) {
            if (after.contains("Thousand") || after.contains("Thousand".toLowerCase()) || after.contains("Thousand".toUpperCase())) {
                putTerm(current, "K", docID);
            } else if (!current.contains("$") && !afterTwo.equals("U.S") && !afterThree.equals("dollars") && (after.contains("Million") || after.contains("Million".toLowerCase()) || after.contains("Million".toUpperCase()))) {
                putTerm(current, "M", docID);
            } else if (!afterThree.equals("dollars") && !afterTwo.equals("U.S") && !current.contains("$") && (after.contains("Billion") || after.contains("Billion".toLowerCase()) || after.contains("Billion".toUpperCase()))) {
                putTerm(current, "B", docID);
            }
            //***************checks if the case is percentage***************************////
            else if (after.contains("percent") || after.contains("percentage") ||
                    after.contains("Percentage") || after.contains("Percent")) {
                putTerm(current, "%", docID);
                return true;
            }
            /***************precent********************************/////
            //checks if expression is mass units
            else if (mass.containsKey(after)) {
                putTerm(current, mass.get(after), docID);
            }
            //checks if expression is electrical units
            else if (electrical.containsKey(after)) {
                putTerm(current, electrical.get(after), docID);
            }

            //checks if expression is date
            else if (Integer.parseInt(current) <= 31 && Integer.parseInt(current) >= 0 && months.containsKey(after)) {
                putTerm(months.get(after) + "-", current, docID);
            }

            //*******************dollars************************************************
            else if (after.equals("Dollars") ||
                    current.contains("$") || (after.equals("billion") && afterTwo.equals("U.S")
                    && afterThree.equals("dollars")) || (after.equals("million") && afterTwo.equals("U.S"))
                    && afterThree.equals("dollars")) {

                if (current.contains("$")) {
                    current = current.substring(1);
                    String numDub = current.replaceAll(",", "");
                    if ((Double.parseDouble(numDub) < 1000000)) {
                        if (!after.equals("million") && !after.equals("billion")) {
                            putTerm(current, " Dollars", docID);
                            return true;
                        } else if (after.equals("million")) {
                            putTerm(current, " M Dollars", docID);
                            return true;
                        } else if (after.equals("billion")) {
                            putTerm(current + "000", " M Dollars", docID);
                            return true;
                        }
                    } else if (Double.parseDouble(numDub) >= 1000000) {
                        current = format(current);
                        putTerm(current, " M Dollars", docID);
                        return true;
                    }
                }////$$$$
                else if (after.equals("Dollars")) {
                    if (current.contains("m")) {
                        current = current.substring(0, current.length() - 1);
                        putTerm(current, " M Dollars", docID);
                        return true;
                    } else if (current.contains("bn")) {
                        current = current.substring(0, current.length() - 2);
                        putTerm(current + "000", " M Dollars", docID);
                        return true;
                    }
                    String numDub = current.replaceAll(",", "");
                    if (!current.contains("/") && Double.parseDouble(numDub) >= 1000000) {
                        current = format(current);
                        putTerm(current, " M Dollars", docID);
                        return true;
                    } else if (!current.contains("/") && Double.parseDouble(numDub) < 1000000) {
                        putTerm(current, " Dollars", docID);
                        return true;
                    } else if (isNumber(before) && current.contains("/")) {
                        putTerm(before + " " + current, " Dollars", docID);
                        return true;
                    }
                }////////*******dollars********///////////////////
                else if (isNumber(current) && after.equals("billion") && afterTwo.equals("U.S") && afterThree.equals("dollars")) {
                    putTerm(current + "000", " M Dollars", docID);
                    return true;
                } else if (isNumber(current) && after.equals("million") && afterTwo.equals("U.S") && afterThree.equals("dollars")) {
                    putTerm(current, " M Dollars", docID);
                    return true;
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

                    current = format(current);///*****////

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
                        if (termMap.get(current).containsKey(docID)) {
                            termMap.get(current).put(docID, termMap.get(current).remove(docID) + 1);
                        } else {
                            termMap.get(current).put(docID, 1);

                        }
                    } else {
                        termMap.put(current, new HashMap<String, Integer>());
                        termMap.get(current).put(docID, 1);
                    }
                }
            } else if (Double.parseDouble(current) < 1000) {
                if (!after.contains("/")) {
                    putTerm(current, "", docID);
                } else if (after.contains("/") && !afterTwo.equals("Dollars")) {
                    putTerm(current, " " + after, docID);
                    tokens.remove(index + 1);
                }
            }
            return true;
        }
        return false;
    }

    /**
     * ido create put term
     *
     * @param current
     * @param character
     * @param docId
     */
    private void putTerm(String current, String character, String docId) {
        if (termMap.containsKey(current + character)) {
            if (termMap.get(current + character).containsKey(docId)) {
                termMap.get(current + character).put(docId, termMap.get(current + character).remove(docId) + 1);
                updateMaxTf(current,character,docId);
                updateWordList(current,character);

            } else {
                termMap.get(current + character).put(docId, 1);
                updateMaxTf(current,character,docId);
                updateWordList(current,character);
            }

        } else {
            termMap.put(current + character, new HashMap<String, Integer>());
            termMap.get(current + character).put(docId, 1);
            updateMaxTf(current,character,docId);
            updateWordList(current,character);
        }
    }

    public void updateMaxTf(String current, String character, String docID){
        if(maxTf.containsKey(docID)){
            maxTf.put(docID,Math.max(termMap.get(current + character).get(docID),maxTf.get(docID)));

        }
        else{
            maxTf.put(docID,termMap.get(current + character).get(docID));
        }
    }

    public void updateWordList(String current, String character){
        if(!termsInDoc.contains(current + character)){
            termsInDoc.add(current + character);
        }

    }

    public boolean stringHandler(ArrayList<String> tokens, int index, String docID) throws ParseException {

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

        if (months.containsKey(current) || !stopwords.contains(current.toLowerCase())) {
            int num = -1;
            try {
                num = Integer.parseInt(after);
                if (months.containsKey(current)) {
                    if (num > 0 && num <= 31) {
                        if (termMap.containsKey(months.get(current) + "-" + after)) {
                            if (termMap.get(months.get(current) + "-" + after).containsKey(docID)) {
                                termMap.get(months.get(current) + "-" + after).put(docID, termMap.get(months.get(current) + "-" + after).get(docID) + 1);
                            } else {
                                termMap.get(months.get(current) + "-" + after).put(docID, 1);
                            }

                        } else {
                            termMap.put(months.get(current) + "-" + after, new HashMap<String, Integer>());
                            termMap.get(months.get(current) + "-" + after).put(docID, 1);
                        }

                    } else if (num > 1900 && isValidDate(after)) {
                        if (months.containsKey(after + "-" + months.get(current))) {
                            if (termMap.get(after + "-" + months.get(current)).containsKey(docID)) {
                                termMap.get(after + "-" + months.get(current)).put(docID, termMap.get(months.get(current) + "-" + after).get(docID) + 1);
                            } else {
                                termMap.get(after + "-" + months.get(current)).put(docID, 1);
                            }

                        } else {
                            termMap.put(after + "-" + months.get(current), new HashMap<String, Integer>());
                            termMap.get(after + "-" + months.get(current)).put(docID, 1);
                        }
                    }

                }
            } catch (NumberFormatException e) {
                //term is not a date

            }
            /***lower/upper**////
            if (Character.isUpperCase(current.charAt(0))) {
                if (termMap.containsKey(current.toLowerCase())) {
                    putTermString(current.toLowerCase(), docID, stemming);
                } else if (termMap.containsKey(current.toUpperCase())) {
                    putTermString(current.toUpperCase(), docID, stemming);
                } else {
                    simpleInsert(current.toUpperCase(), docID, stemming);
                    return true;
                }
            } else if (Character.isLowerCase(current.charAt(0))) {
                if (termMap.containsKey(current.toUpperCase())) {
                    termMap.put(current.toLowerCase(), termMap.remove(current.toUpperCase())); // remove uppercase key and update to lowercase key
                    putTermString(current.toLowerCase(), docID, stemming);
                    return true;

                } else {
                    simpleInsert(current, docID, stemming);
                    return true;
                }
            } else {
                simpleInsert(current, docID, stemming);
                return true;
            }
            return true;
        }

        return false;
    }

    private void simpleInsert(String current, String docID, boolean stem) {
        if (stem == true) {
            porterStemmer porter = new porterStemmer();
            porter.setCurrent(current);
            porter.stem();
            current = porter.getCurrent();
        }
        termMap.put(current, new HashMap<String, Integer>());
        termMap.get(current).put(docID, 1);
    }

    private void putTermString(String current, String docID, boolean stem) {
        if (stem == true) {
            porterStemmer porter = new porterStemmer();
            porter.setCurrent(current);
            porter.stem();
            current = porter.getCurrent();
        }
        if (termMap.get(current).containsKey(docID)) {
            termMap.get(current).put(docID, termMap.get(current).remove(docID) + 1);
        } else {
            termMap.get(current).put(docID, 1);
        }
    }

    public boolean isValidDate(String dateStr) {
        DateFormat sdf = new SimpleDateFormat("YYYY");
        sdf.setLenient(false);
        try {
            sdf.parse(dateStr);
        } catch (ParseException e) {
            return false;
        }
        return true;
    }

    /**
     * ido add this function that change the format of the number
     *
     * @param current
     * @return
     * @throws ParseException
     */
    private String format(String current) throws ParseException {
        NumberFormat format = NumberFormat.getInstance(Locale.FRANCE);
        Number number = format.parse(current);
        double d = number.doubleValue();
        if (d == (long) d)
            return String.format("%d", (long) d);
        else
            return String.format("%s", d);
    }

}