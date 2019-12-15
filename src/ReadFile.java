

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class ReadFile implements  Runnable{
    protected ArrayList<String> allFile;
    private File subFolder = null;
    private String[] splits;
    private Indexer index;
    private boolean stem;

    public ReadFile(File subFolder,Indexer i,boolean stemming) throws IOException {
        allFile = new ArrayList<>();
        this.subFolder = subFolder;
        index=i;
        stem = stemming;
    }
    @Override
    public void run() {
        Scanner file3 = null;
        int counter = 0;//delete
        File[] files1 = null;
        files1 = subFolder.listFiles();
        for (File file2 : files1) {
            String TxtPaths = file2.getPath();
            try {
                file3 = new Scanner(file2);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            finally {
                file3.close();
            }
            String text = null;
            try {
                text = new String(Files.readAllBytes(Paths.get(TxtPaths)), StandardCharsets.UTF_8);
            } catch (IOException e) {
                e.printStackTrace();
            }
            splits = text.split("</DOC>");
            allFile.addAll(Arrays.asList(splits));//
            counter = counter + splits.length - 1;//delete
        }
        try {
            Parser p = new Parser(true,this);
            p.parseDocs(splits,index);
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public File getSubFolder() {
        return subFolder;
    }

    public void setSubFolder(File subFolder) {
        this.subFolder = subFolder;
    }

}
