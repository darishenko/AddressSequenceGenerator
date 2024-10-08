package by.darishenko.addressSequenceGenerator;

import by.darishenko.addressSequenceGenerator.controller.AddressSequenceCharBarController;
import by.darishenko.addressSequenceGenerator.controller.SetAnimationSpeedController;
import by.darishenko.addressSequenceGenerator.converter.BinaryConverter;
import by.darishenko.addressSequenceGenerator.exception.MyException;
import by.darishenko.addressSequenceGenerator.generator.MainGenerator;
import by.darishenko.addressSequenceGenerator.validator.StringValidator;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

import static by.darishenko.addressSequenceGenerator.converter.BinaryConverter.convertBinaryStringsToDigits;
import static by.darishenko.addressSequenceGenerator.converter.BinaryConverter.convertDigitsToBinaryStrings;

public class MainController {
    private List<Stage> childStages = new ArrayList<>();

    private final MainGenerator mainGenerator = new MainGenerator(0);
    List<String> generatingMatrixFromFile = null;
    private File file = null;
    private List<String> binaryAddressSequence = null;
    private List<Integer> decimalAddressSequence = null;
    private boolean canStart = false;

    public int getAnimationSpeed() {
        return animationSpeed;
    }

    public void setAnimationSpeed(int animationSpeed) {
        this.animationSpeed = animationSpeed;
    }

    private int animationSpeed = 100;

    @FXML
    private Label L_openFile;
    @FXML
    private TextArea ta_generatingMatrix;
    @FXML
    private Label l_initialStateLength;
    @FXML
    private TextField tf_initialState;
    @FXML
    private Button b_generateSequence;
    @FXML
    private CheckMenuItem chMI_writeGenerateMatrixToTextArea;
    @FXML
    private CheckMenuItem chMI_writeAddressSequenceToTextArea;
    @FXML
    private TextArea ta_generatedAddressSequence;



    public void showWarningMessage(String Title, String HeaderText, String ContentText) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(Title);
        alert.setHeaderText(HeaderText);
        alert.setContentText(ContentText);
        alert.showAndWait();
    }

    public void showInformationMessage(String Title, String ContentText) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(Title);
        alert.setContentText(ContentText);
        alert.showAndWait();
    }

    private void writeGeneratingMatrix(List<String> matrix, TextArea textArea) {
        textArea.clear();
        if (matrix.size() > 0) {
            for (int i = 0; i < matrix.size() - 1; i++) {
                textArea.appendText(matrix.get(i) + System.getProperty("line.separator"));
            }
            textArea.appendText(matrix.get(matrix.size() - 1));
        }
    }

    @FXML
    void openFile(ActionEvent event) {
        try {
            file = FileWorker.chooseSingleFileToOpen("Текстовый файл", "txt");
            if (file != null) {
                generatingMatrixFromFile = FileWorker.readFileLines(file);
                L_openFile.setText("Открытый файл " + file.getName());
                if (chMI_writeGenerateMatrixToTextArea.isSelected()) {
                    writeGeneratingMatrix(generatingMatrixFromFile, ta_generatingMatrix);
                }
            }
        } catch (IOException e) {
            showWarningMessage("Warning", "Файл не найден", "Повторите попытку");
        }
    }

    private void saveGeneratingMatrixAsToFile() throws IOException {
        if (ta_generatingMatrix.getText().isEmpty()) {
            showWarningMessage("Warning", "Файл не сохранен", "Порождающая матрица не заполнена");
            return;
        }
        if (!canStart) {
            showWarningMessage("Warning", "Файл не сохранен", "Порождающая матрица содержит недопустимые символы");
            return;
        }

        if (file != null) {
            File chosenFile = FileWorker.chooseSingleFileToSave(file.getParent(), file.getName(), "result_");
            FileWorker.writeToFileBinary(chosenFile, ta_generatingMatrix.getText());
        } else {
            String strUserDirectory = System.getProperty("user.dir");
            File chosenFile = FileWorker.chooseSingleFileToSave(strUserDirectory, ".txt", "");
            FileWorker.writeToFileBinary(chosenFile, ta_generatingMatrix.getText());
        }
    }

    private void saveAddressSequenceAsToFile() throws IOException {
        if (binaryAddressSequence == null || binaryAddressSequence.isEmpty()) {
            showWarningMessage("Warning", "Файл не сохранен", "Адресная последовательность еще не сгенерирована");
            return;
        }

        if (file != null) {
            File chosenFile = FileWorker.chooseSingleFileToSave(file.getParent(), file.getName(), "2_result_");
            FileWorker.writeToFileBinary(chosenFile, binaryAddressSequence);
            chosenFile = FileWorker.chooseSingleFileToSave(file.getParent(), file.getName(), "10_result_");
            FileWorker.writeToFileDecimal(chosenFile, decimalAddressSequence);
        } else {
            String strUserDirectory = System.getProperty("user.dir");
            File chosenFile = FileWorker.chooseSingleFileToSave(strUserDirectory, ".txt", "2_result_");
            FileWorker.writeToFileBinary(chosenFile, binaryAddressSequence);
            chosenFile = FileWorker.chooseSingleFileToSave(strUserDirectory, ".txt", "10_result_");
            FileWorker.writeToFileDecimal(chosenFile, decimalAddressSequence);
        }
    }

    @FXML
    void saveAsToFile(ActionEvent event) {
        MenuItem initiator = (MenuItem) event.getSource();
        String initiatorId = initiator.getId();
        try {
            switch (initiatorId) {
                case "MI_SavePMatrix" -> saveGeneratingMatrixAsToFile();
                case "MI_saveAdrSequence" -> saveAddressSequenceAsToFile();
            }
        } catch (IOException e) {
            showWarningMessage("Warning", "Файл не сохранен", "Повторите попытку");
        }
    }

    @FXML
    void saveToFile(ActionEvent event) throws IOException {
        MenuItem initiator = (MenuItem) event.getSource();
        String initiatorId = initiator.getId();

        if (file != null) {
            try {
                switch (initiatorId) {
                    case "MI_SavePMatrix" -> {
                        if (canStart) {
                            FileWorker.writeToFileBinary(file, ta_generatingMatrix.getText());
                        } else {
                            showWarningMessage("Warning", "Файл не сохранен", "Порождающая матрица содержит недопустимые символы");
                        }
                    }
                    case "MI_saveAdrSequence" -> {
                        if (binaryAddressSequence != null && !binaryAddressSequence.isEmpty()) {
                            File binaryFile = new File(file.getParent(), "2_result_"+ file.getName());
                            FileWorker.writeToFileBinary(binaryFile, binaryAddressSequence);
                            File decimalFile = new File(file.getParent(), "10_result_"+ file.getName());
                            FileWorker.writeToFileDecimal(decimalFile, decimalAddressSequence);
                        } else {
                            showWarningMessage("Warning", "Файл не сохранен", "Адресная последовательность еще не сгенерирована");
                        }
                    }
                }
            } catch (IOException e) {
                showWarningMessage("Warning", "Файл не сохранен", "Повторите попытку");
            }
        } else {
            saveAsToFile(event);
        }
    }

    private int findMaxLength(List<String> generatingMatrix){
        String maxElement = Collections.max(generatingMatrix, Comparator.comparing(String::length));
        return maxElement.length();
    }

    @FXML
    void GenerateSequence() throws IOException {
        List<String> generatingMatrix;
        if (chMI_writeGenerateMatrixToTextArea.isSelected()) {
            String ta_generatingMatrixText = StringValidator.removeSpacesFromLine(ta_generatingMatrix.getText());
            generatingMatrix = List.of(ta_generatingMatrixText.split("[\n]+"));
        } else {
            generatingMatrix = generatingMatrixFromFile;
        }

        if (generatingMatrixFromFile != null || (generatingMatrix != null && !generatingMatrix.isEmpty()) ) {
            String initialState = StringValidator.removeSpacesFromLine(tf_initialState.getText());
            try {
                mainGenerator.setLength(generatingMatrix.size());
                if (initialState.isEmpty()) {
                    mainGenerator.setInitialState(0);
                } else {
                    mainGenerator.setInitialState(BinaryConverter.convertBinaryStringToDigit(initialState));
                }

                decimalAddressSequence = mainGenerator.generateSequence(convertBinaryStringsToDigits(generatingMatrix));
                int maxElemLength = findMaxLength(generatingMatrix);

                if (maxElemLength > generatingMatrix.size()){
                    showWarningMessage("Warning", "Порождающая матрица некорректна", "Число строк не может быть меньше числа столбцов");
                    return;
                }

                binaryAddressSequence = convertDigitsToBinaryStrings(decimalAddressSequence, maxElemLength);
                ta_generatedAddressSequence.clear();
                if (chMI_writeAddressSequenceToTextArea.isSelected()) {
                    writeGeneratingMatrix(binaryAddressSequence, ta_generatedAddressSequence);
                } else {
                    showInformationMessage("Процесс завершен", "Адресная последовательность сгенерирована и готова к сохранению");
                }

                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("addressSequenceCharBar.fxml"));
                loader.load();
                Parent root = loader.getRoot();
                AddressSequenceCharBarController children = loader.getController();
                children.setParent(this);
                children.setAddressSequence(decimalAddressSequence);
                children.setAnimationSpeed(animationSpeed);
                Stage stage = new Stage();
                stage.setTitle(children.getTitle());
                stage.setResizable(children.getIsResizable());
                stage.setScene(new Scene(root));
                children.setStage(stage);
                children.startAnimationAtFirst();
                childStages.add(stage);
                stage.showAndWait();

            } catch (MyException e) {
                showWarningMessage(e.getMessage(), e.getMessageCorrection(), e.getMessageAdvice());
            }
        } else {
            showWarningMessage("Warning", "Порождающая матрица пустая", "Выберете файл с порождающей матрицей");
        }
    }

    @FXML
    void setAnimationSpeed(){
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("setAnimationSpeed.fxml"));
            loader.load();
            Parent root = loader.getRoot();
            SetAnimationSpeedController children = loader.getController();
            children.setParent(this);
            Stage stage = new Stage();
            stage.setTitle(children.getTitle());
            stage.setResizable(children.getIsResizable());
            stage.setScene(new Scene(root));
            children.setStage(stage);
            childStages.add(stage);
            children.setCurrentAnimationSpeed(animationSpeed);
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void closeAll() {
            for (Stage stage : childStages){
                stage.close();
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
                showWarningMessage("Warning", "Файл не сохранен", "Повторите попытку");
            }

            b_generateSequence.setDisable(errorValidationCounter != 0);
            canStart = (errorValidationCounter == 0);
        });
        ta_generatingMatrix.textProperty().addListener(validationGeneratingSequenceListener);
        tf_initialState.textProperty().addListener(validationGeneratingSequenceListener);

        chMI_writeGenerateMatrixToTextArea.selectedProperty().addListener((observableValue, aBoolean, t1) -> {
            b_generateSequence.setDisable(false);
            ta_generatingMatrix.setDisable(aBoolean);
        });
        chMI_writeAddressSequenceToTextArea.selectedProperty().addListener((observableValue, aBoolean, t1) -> {
            ta_generatedAddressSequence.setDisable(aBoolean);
        });

        tf_initialState.textProperty().addListener((observable, oldValue, newValue) -> {
            l_initialStateLength.setText(String.valueOf(newValue.length()));
        });

    }
}