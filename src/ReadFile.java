
import java.io.*;/////
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
public class ReadFile {
    public static void main(String[] args) throws IOException {
        System.out.println("Enter the path to search");
        String doc = "";
        int counter=0;
        List<String> allFile = new ArrayList<>();
        Scanner scanner = new Scanner(System.in);
        String folderPath = scanner.next();
        File [] files1 = null;
        File folder = new File(folderPath);
        if (folder.isDirectory()) {
            File[] listOfSubFolders = folder.listFiles();
            System.out.println(listOfSubFolders.length);
            for (File SubFolder : listOfSubFolders) {
                if(SubFolder.isDirectory()) {
                    files1 = SubFolder.listFiles();
                    for(File file2 : files1) {

                        String TxtPaths = file2.getPath();
                        Scanner file3 = new Scanner(file2);
                        String text = new String(Files.readAllBytes(Paths.get(TxtPaths)), StandardCharsets.UTF_8);
                        String[] splits = text.split("</DOC>");
                        counter = counter + splits.length-1;

                    }
                }
            }
        }
        System.out.println(counter);
    }
}
