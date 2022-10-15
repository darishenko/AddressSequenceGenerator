package by.darishenko.addressSequenceGenerator;

import by.darishenko.addressSequenceGenerator.generator.MainGenerator;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.File;
import java.io.IOException;
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
            showWarningMessage("Warning", "Файл не найден", "Повторите попытку");
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
                showWarningMessage("Warning", "Файл не сохранен", "Повторите попытку");
            }
        } else {
            String strUserDirectory = System.getProperty("user.dir");
            File chosenFile = FileWorker.chooseSingleFileToSave(strUserDirectory, ".txt", "");
            FileWorker.writeToFile(chosenFile, generatedSequence);
        }
    }

    @FXML
    void saveToFile(ActionEvent event) throws IOException {
        if (file != null) {
            try {
                FileWorker.writeToFile(file, generatedSequence);
            } catch (IOException e) {
                showWarningMessage("Warning", "Файл не сохранен", "Повторите попытку");
            }
        } else {
            saveAsToFile();
        }
    }

    @FXML
    void initialize() {
        b_generateSequence.setDisable(true);

        Pattern patternInitialState = Pattern.compile("([01\s]+)*");
        Pattern patternGeneratingMatrix = Pattern.compile("([01]|(\s*[01]+)+[\n\s]*)+");
        ChangeListener<String> validationGeneratingSequenceListener = ((observable, oldValue, newValue) -> {
            int errorValidationCounter = 0;

            if (patternInitialState.matcher(tf_initialState.getText()).matches()) {
                tf_initialState.setStyle(null);
            } else {
                tf_initialState.setStyle("-fx-border-color: red ; fx-border-width: 2px");
                errorValidationCounter++;
                b_generateSequence.setDisable(true);
            }

            if (patternGeneratingMatrix.matcher(ta_generatingMatrix.getText()).matches()) {
                ta_generatingMatrix.setStyle(null);
            } else {
                ta_generatingMatrix.setStyle("-fx-border-color: red ; fx-border-width: 2px");
                errorValidationCounter++;
                b_generateSequence.setDisable(true);
            }

            if (errorValidationCounter == 0) {
                b_generateSequence.setDisable(false);
            }
        });
        ta_generatingMatrix.textProperty().addListener(validationGeneratingSequenceListener);
        tf_initialState.textProperty().addListener(validationGeneratingSequenceListener);


    }
}