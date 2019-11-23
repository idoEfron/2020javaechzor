
import java.io.*;/////
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
public class ReadFile {
    protected ArrayList<String>allFile;

    public ReadFile() throws IOException {
        allFile = new ArrayList<>();
        int counter = 0;//delete
        Scanner scanner = new Scanner(System.in);
        String folderPath = scanner.next();
        File[] files1 = null;
        File folder = new File(folderPath);
        if (folder.isDirectory()) {
            File[] listOfSubFolders = folder.listFiles();
            System.out.println(listOfSubFolders.length);
            for (File SubFolder : listOfSubFolders) {
                if (SubFolder.isDirectory()) {
                    files1 = SubFolder.listFiles();
                    for (File file2 : files1) {
                        String TxtPaths = file2.getPath();
                        Scanner file3 = new Scanner(file2);
                        String text = new String(Files.readAllBytes(Paths.get(TxtPaths)), StandardCharsets.UTF_8);
                        String[] splits = text.split("</DOC>");
                        allFile.addAll(Arrays.asList(splits));//
                        counter = counter + splits.length - 1;//delete

                    }
                }
            }
        }
        System.out.println(counter);//delete
        System.out.println(allFile.size() - 1815);//delete
    }
}
