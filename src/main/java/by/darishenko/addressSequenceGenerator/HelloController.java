package by.darishenko.addressSequenceGenerator;

import by.darishenko.addressSequenceGenerator.generator.MainGenerator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Pattern;

public class HelloController {
    private File file = null;
    private List<String> generatedSequence = null;


    @FXML
    private TextArea ta_generatingMatrix;
    @FXML
    private TextField tf_initialState;
    @FXML
    private Button b_generateSequence;

    public void showWarningMessage(String Title, String HeaderText, String ContentText) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(Title);
        alert.setHeaderText(HeaderText);
        alert.setContentText(ContentText);
        alert.showAndWait();
    }

    void writeGeneratingMatrix(List<String> matrix) {
        ta_generatingMatrix.clear();
        for (int i = 0; i < matrix.size() - 1; i++) {
            ta_generatingMatrix.appendText(matrix.get(i) + System.getProperty("line.separator"));
        }
        ta_generatingMatrix.appendText(matrix.get(matrix.size() - 1));
    }

    @FXML
    void openFile(ActionEvent event) {
        try {
            file = FileWorker.chooseSingleFileToOpen("Текстовый файл", "txt");
            if (file != null) {
                List<String> matrix = FileWorker.readFileLines(file);
                writeGeneratingMatrix(matrix);
            }
        } catch (IOException e) {
            showWarningMessage("File ERROR", "Файл не найден", "Повторите попытку");
        }
    }

    @FXML
    void GenerateSequence() {
        String ta_generatingMatrixText = ta_generatingMatrix.getText();
        List<String> generatingMatrix = List.of(ta_generatingMatrixText.split("[\n]+"));
        String initialState = tf_initialState.getText();
        MainGenerator mainGenerator = new MainGenerator(generatingMatrix.size(), BinaryConverter.convertBinaryStringToDigit(initialState));
        List<Integer> resultSequence = mainGenerator.generateSequence(BinaryConverter.convertBinaryStringsToDigits(generatingMatrix));
        generatedSequence = BinaryConverter.convertDigitsToBinaryStrings(resultSequence, initialState.length());
    }

    @FXML
    void saveAsToFile() throws IOException {
        if (file != null) {
            try {
                File chosenFile = FileWorker.chooseSingleFileToSave(file.getParent(), file.getName(), "result_");
                FileWorker.writeToFile(chosenFile, generatedSequence);
            } catch (IOException e) {
                showWarningMessage("File ERROR", "Файл не сохранен", "Повторите попытку");
            }
        } else {
            String userDirectory = System.getProperty("user.dir");
            Path dir = Files.createDirectories(Path.of(userDirectory));
            File chosenFile = FileWorker.chooseSingleFileToSave(userDirectory, ".txt", "");
            FileWorker.writeToFile(chosenFile, generatedSequence);
        }
    }

    @FXML
    void saveToFile(ActionEvent event) throws IOException {
        if (file != null) {
            try {
                FileWorker.writeToFile(file, generatedSequence);
            } catch (IOException e) {
                showWarningMessage("File ERROR", "Файл не сохранен", "Повторите попытку");
            }
        } else {
            saveAsToFile();
        }
    }

    @FXML
    void initialize() {

        b_generateSequence.setDisable(true);

//        Pattern p = Pattern.compile("([01\s]+)*");
//        tf_initialState.textProperty().addListener((observableValue, oldValue, newValue) -> {
//            if (p.matcher(newValue).matches()){
//                tf_initialState.setStyle(null);
//                b_generateSequence.setDisable(false);
//            }else{
//                tf_initialState.setStyle("-fx-border-color: red ; fx-border-width: 2px");
//                b_generateSequence.setDisable(true);
//            }
//        });

        Pattern p2 = Pattern.compile("([01]+[\n\s]*)+");
        ta_generatingMatrix.textProperty().addListener((observableValue, oldValue, newValue) -> {
            if (p2.matcher(newValue).matches() && !ta_generatingMatrix.getText().trim().isEmpty()) {
                ta_generatingMatrix.setStyle(null);
                b_generateSequence.setDisable(false);
            } else {
                ta_generatingMatrix.setStyle("-fx-border-color: red ; fx-border-width: 2px");
                b_generateSequence.setDisable(true);
            }
        });


    }
}