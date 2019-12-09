

import java.io.*;/////
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class ReadFile implements  Runnable{
    protected ArrayList<String> allFile;
    private File subFolder = null;
    private Parser p;

    public ReadFile(File subFolder, Parser p) throws IOException {
        allFile = new ArrayList<>();
        this.subFolder = subFolder;
        this.p = p;
    }
    @Override
    public void run() {
        int counter = 0;//delete
        File[] files1 = null;
        files1 = subFolder.listFiles();
        for (File file2 : files1) {
            String TxtPaths = file2.getPath();
            try {
                Scanner file3 = new Scanner(file2);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            String text = null;
            try {
                text = new String(Files.readAllBytes(Paths.get(TxtPaths)), StandardCharsets.UTF_8);
            } catch (IOException e) {
                e.printStackTrace();
            }
            String[] splits = text.split("</DOC>");
            allFile.addAll(Arrays.asList(splits));//
            counter = counter + splits.length - 1;//delete
        }
        System.out.println(counter);//delete
        try {
            p.parseDocs(allFile);
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
