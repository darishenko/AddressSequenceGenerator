package by.darishenko.addressSequenceGenerator;

import by.darishenko.addressSequenceGenerator.exception.MyException;
import by.darishenko.addressSequenceGenerator.generator.MainGenerator;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

public class HelloController {
    List<String> generatingMatrixFromFile = null;
    private File file = null;
    private List<String> addressSequence = null;


    @FXML
    private TextArea ta_generatingMatrix;
    @FXML
    private TextField tf_initialState;
    @FXML
    private Button b_generateSequence;
    @FXML
    private CheckMenuItem chMI_writeGenerateMatrixToTextArea;
    @FXML
    private TextArea ta_generatedAddressSequence;

    public void showWarningMessage(String Title, String HeaderText, String ContentText) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(Title);
        alert.setHeaderText(HeaderText);
        alert.setContentText(ContentText);
        alert.showAndWait();
    }

    void writeGeneratingMatrix(List<String> matrix, TextArea textArea) {
        textArea.clear();
        for (int i = 0; i < matrix.size() - 1; i++) {
            textArea.appendText(matrix.get(i) + System.getProperty("line.separator"));
        }
        textArea.appendText(matrix.get(matrix.size() - 1));
    }

    @FXML
    void openFile(ActionEvent event) {
        try {
            file = FileWorker.chooseSingleFileToOpen("Текстовый файл", "txt");
            if (file != null) {
                generatingMatrixFromFile = FileWorker.readFileLines(file);
                if (chMI_writeGenerateMatrixToTextArea.isSelected()) {
                    writeGeneratingMatrix(generatingMatrixFromFile, ta_generatingMatrix);
                }
            }
        } catch (IOException e) {
            showWarningMessage("Warning", "Файл не найден", "Повторите попытку");
        }
    }

    @FXML
    void GenerateSequence() {
        List<String> generatingMatrix;
        if (chMI_writeGenerateMatrixToTextArea.isSelected()) {
            String ta_generatingMatrixText = Validator.removeSpacesFromLine(ta_generatingMatrix.getText());
            generatingMatrix = List.of(ta_generatingMatrixText.split("[\n]+"));
        } else {
            generatingMatrix = generatingMatrixFromFile;
        }

        String initialState = Validator.removeSpacesFromLine(tf_initialState.getText());
        MainGenerator mainGenerator;
        try {
            if (initialState.isEmpty()) {
                mainGenerator = new MainGenerator(generatingMatrix.size());
            } else {
                mainGenerator = new MainGenerator(generatingMatrix.size(), BinaryConverter.convertBinaryStringToDigit(initialState));
            }
            List<Integer> generatedAddressSequence;
            generatedAddressSequence = mainGenerator.generateSequence(BinaryConverter.convertBinaryStringsToDigits(generatingMatrix));
            addressSequence = BinaryConverter.convertDigitsToBinaryStrings(generatedAddressSequence, initialState.length());
            ta_generatedAddressSequence.clear();
            writeGeneratingMatrix(addressSequence, ta_generatedAddressSequence);
        } catch (MyException e) {
            showWarningMessage("Warning", "Файл не сохранен", "Повторите попытку");
        }
    }

    @FXML
    void saveAsToFile() throws IOException {
        if (file != null) {
            try {
                File chosenFile = FileWorker.chooseSingleFileToSave(file.getParent(), file.getName(), "result_");
                FileWorker.writeToFile(chosenFile, addressSequence);
            } catch (IOException e) {
                showWarningMessage("Warning", "Файл не сохранен", "Повторите попытку");
            }
        } else {
            String strUserDirectory = System.getProperty("user.dir");
            File chosenFile = FileWorker.chooseSingleFileToSave(strUserDirectory, ".txt", "");
            FileWorker.writeToFile(chosenFile, addressSequence);
        }
    }

    @FXML
    void saveToFile(ActionEvent event) throws IOException {
        if (file != null) {
            try {
                FileWorker.writeToFile(file, addressSequence);
                System.out.println(addressSequence);
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
            try {
                if (chMI_writeGenerateMatrixToTextArea.isSelected()) {
                    if (patternInitialState.matcher(tf_initialState.getText()).matches()) {
                        tf_initialState.setStyle(null);
                    } else {
                        tf_initialState.setStyle("-fx-border-color: red ; fx-border-width: 2px");
                        errorValidationCounter++;
                    }

                    if (patternGeneratingMatrix.matcher(ta_generatingMatrix.getText()).matches()) {
                        ta_generatingMatrix.setStyle(null);
                    } else {
                        ta_generatingMatrix.setStyle("-fx-border-color: red ; fx-border-width: 2px");
                        errorValidationCounter++;
                    }
                }
            } catch (StackOverflowError e) {
                //showWarningMessage("Warning", "Файл не сохранен", "Повторите попытку");
            }
            b_generateSequence.setDisable(errorValidationCounter != 0);
        });
        ta_generatingMatrix.textProperty().addListener(validationGeneratingSequenceListener);
        tf_initialState.textProperty().addListener(validationGeneratingSequenceListener);

        chMI_writeGenerateMatrixToTextArea.selectedProperty().addListener((observableValue, aBoolean, t1) -> {
            b_generateSequence.setDisable(!aBoolean);
        });

    }
}