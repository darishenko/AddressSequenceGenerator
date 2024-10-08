package by.darishenko.addressSequenceGenerator;

import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileWorker {

    public static File chooseSingleFileToOpen(String fileInfo, String extension) throws IOException {
        Stage stage = new Stage();
        String userDirectory = System.getProperty("user.dir");
        Path dir = Files.createDirectories(Path.of(userDirectory));
        FileChooser fc = new FileChooser();
        fc.setInitialDirectory(dir.toFile());
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter(fileInfo, "*." + extension));
        return fc.showOpenDialog(stage);
    }

    public static File chooseSingleFileToSave(String userDirectory, String fileName, String extBeforeFileName) throws IOException {
        Stage stage = new Stage();
        FileChooser fc = new FileChooser();
        Path dir = Files.createDirectories(Path.of(userDirectory));
        fc.setInitialDirectory(dir.toFile());
        fc.setInitialFileName(extBeforeFileName + fileName);
        return fc.showSaveDialog(stage);
    }


    public static List<String> readFileLines(File file) throws IOException {
        List<String> fileLines = new ArrayList<>();
        if (file != null && file.exists()) {
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String currentString;
            while ((currentString = bufferedReader.readLine()) != null) {
                fileLines.add(currentString);
            }
        }
        return fileLines;
    }

    public static void writeToFileBinary(File file, List<String> sequence) throws IOException {
        if (file != null) {
            FileWriter writer = new FileWriter(file);
            BufferedWriter bufferedWriter = new BufferedWriter(writer);
            for (String element : sequence) {
                bufferedWriter.write(element + System.getProperty("line.separator"));
            }
            bufferedWriter.close();
            writer.close();
        }
    }

    public static void writeToFileDecimal(File file, List<Integer> sequence) throws IOException {
        if (file != null) {
            FileWriter writer = new FileWriter(file);
            BufferedWriter bufferedWriter = new BufferedWriter(writer);
            for (Integer element : sequence) {
                bufferedWriter.write(element + System.getProperty("line.separator"));
            }
            bufferedWriter.close();
            writer.close();
        }
    }

    public static void writeToFileBinary(File file, String sequence) throws IOException {
        if (file != null) {
            FileWriter writer = new FileWriter(file);
            BufferedWriter bufferedWriter = new BufferedWriter(writer);
            bufferedWriter.write(sequence);
            bufferedWriter.close();
            writer.close();
        }
    }
}
