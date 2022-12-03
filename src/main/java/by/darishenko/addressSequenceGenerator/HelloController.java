package by.darishenko.addressSequenceGenerator;

import by.darishenko.addressSequenceGenerator.exception.MyException;
import by.darishenko.addressSequenceGenerator.generator.MainGenerator;
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
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static by.darishenko.addressSequenceGenerator.BinaryConverter.convertBinaryStringsToDigits;
import static by.darishenko.addressSequenceGenerator.BinaryConverter.convertDigitsToBinaryStrings;

public class HelloController {
    private List<Stage> childStages = new ArrayList<>();

    private final MainGenerator mainGenerator = new MainGenerator(0);
    List<String> generatingMatrixFromFile = null;
    private File file = null;
    private List<String> addressSequence = null;
    private boolean canStart = false;

    public int getAnimationSpeed() {
        return animationSpeed;
    }

    public void setAnimationSpeed(int animationSpeed) {
        this.animationSpeed = animationSpeed;
    }

    private int animationSpeed = 100;
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
            FileWorker.writeToFile(chosenFile, ta_generatingMatrix.getText());
        } else {
            String strUserDirectory = System.getProperty("user.dir");
            File chosenFile = FileWorker.chooseSingleFileToSave(strUserDirectory, ".txt", "");
            FileWorker.writeToFile(chosenFile, ta_generatingMatrix.getText());
        }
    }

    private void saveAddressSequenceAsToFile() throws IOException {
        if (addressSequence == null || addressSequence.isEmpty()) {
            showWarningMessage("Warning", "Файл не сохранен", "Адресная последовательность еще не сгенерирована");
            return;
        }

        if (file != null) {
            File chosenFile = FileWorker.chooseSingleFileToSave(file.getParent(), file.getName(), "result_");
            FileWorker.writeToFile(chosenFile, addressSequence);
        } else {
            String strUserDirectory = System.getProperty("user.dir");
            File chosenFile = FileWorker.chooseSingleFileToSave(strUserDirectory, ".txt", "");
            FileWorker.writeToFile(chosenFile, addressSequence);
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
                            FileWorker.writeToFile(file, ta_generatingMatrix.getText());
                        } else {
                            showWarningMessage("Warning", "Файл не сохранен", "Порождающая матрица содержит недопустимые символы");
                        }
                    }
                    case "MI_saveAdrSequence" -> {
                        if (addressSequence != null && !addressSequence.isEmpty()) {
                            FileWorker.writeToFile(file, addressSequence);
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

    @FXML
    void GenerateSequence() throws IOException {
        List<String> generatingMatrix;
        if (chMI_writeGenerateMatrixToTextArea.isSelected()) {
            String ta_generatingMatrixText = Validator.removeSpacesFromLine(ta_generatingMatrix.getText());
            generatingMatrix = List.of(ta_generatingMatrixText.split("[\n]+"));
        } else {
            generatingMatrix = generatingMatrixFromFile;
        }

        if (generatingMatrixFromFile != null || (generatingMatrix != null && !generatingMatrix.isEmpty()) ) {
            String initialState = Validator.removeSpacesFromLine(tf_initialState.getText());
            try {
                mainGenerator.setLength(generatingMatrix.size());
                if (initialState.isEmpty()) {
                    mainGenerator.setInitialState(0);
                } else {
                    mainGenerator.setInitialState(BinaryConverter.convertBinaryStringToDigit(initialState));
                }
                List<Integer> generatedAddressSequence;
                generatedAddressSequence = mainGenerator.generateSequence(convertBinaryStringsToDigits(generatingMatrix));
                addressSequence = convertDigitsToBinaryStrings(generatedAddressSequence, initialState.length());
                ta_generatedAddressSequence.clear();
                if (chMI_writeAddressSequenceToTextArea.isSelected()) {
                    writeGeneratingMatrix(addressSequence, ta_generatedAddressSequence);
                } else {
                    showInformationMessage("Процесс завершен", "Адресная последовательность сгенерирована и готова к сохранению");
                }

                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("addressSequenceCharBar.fxml"));
                loader.load();
                Parent root = loader.getRoot();
                AddressSequenceCharBarController children = loader.getController();
                Stage stage = new Stage();
                stage.setScene(new Scene(root));

                children.setBarChart_AddressSequence(generatedAddressSequence, animationSpeed);
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
                //showWarningMessage("Warning", "Файл не сохранен", "Повторите попытку");
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