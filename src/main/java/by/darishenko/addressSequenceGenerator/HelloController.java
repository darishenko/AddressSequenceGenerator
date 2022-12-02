package by.darishenko.addressSequenceGenerator;

import by.darishenko.addressSequenceGenerator.exception.MyException;
import by.darishenko.addressSequenceGenerator.generator.MainGenerator;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import static by.darishenko.addressSequenceGenerator.BinaryConverter.convertBinaryStringsToDigits;
import static by.darishenko.addressSequenceGenerator.BinaryConverter.convertDigitsToBinaryStrings;
import static java.lang.Integer.parseInt;

public class HelloController {
    private final MainGenerator mainGenerator = new MainGenerator(0);
    List<String> generatingMatrixFromFile = null;
    private File file = null;
    private List<String> addressSequence = null;
    private boolean canStart = false;
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

    public void showInformationMessage(String Title,  String ContentText) {
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
    void GenerateSequence() {
        List<String> generatingMatrix;
        if (chMI_writeGenerateMatrixToTextArea.isSelected()) {
            String ta_generatingMatrixText = Validator.removeSpacesFromLine(ta_generatingMatrix.getText());
            generatingMatrix = List.of(ta_generatingMatrixText.split("[\n]+"));
        } else {
            generatingMatrix = generatingMatrixFromFile;
        }

        if (generatingMatrixFromFile != null || generatingMatrix != null || !generatingMatrix.isEmpty()) {
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
                }else{
                    showInformationMessage("Процесс завершен", "Адресная последовательность сгенерирована и готова к сохранению");
                }

                createBarChar(generatedAddressSequence);
            } catch (MyException e) {
                showWarningMessage(e.getMessage(), e.getMessageCorrection(), e.getMessageAdvice());
            }
        } else {
            showWarningMessage("Warning", "Порождающая матрица пустая", "Выберете файл с порождающей матрицей");
        }
    }


    //#todo
    private void createBarChar(List<Integer> sequence) {
        HashMap<Integer, Integer> sequenceHashMap = new HashMap<>();
        for (int elem : sequence) {
            if (!sequenceHashMap.containsKey(elem)) {
                sequenceHashMap.put(elem, 1);
            } else {
                sequenceHashMap.put(elem, sequenceHashMap.get(elem) + 1);
            }
        }
        ArrayList<Integer> valueTimes = new ArrayList<>(sequenceHashMap.values());
        ArrayList<Integer> values = new ArrayList<>(sequenceHashMap.keySet());
        Collections.sort(valueTimes);
        Collections.sort(values);
        int maxY = valueTimes.get(valueTimes.size() - 1);
        //
        System.out.println(maxY);
        System.out.println(valueTimes);
        System.out.println(sequenceHashMap);
        //

        CategoryAxis x = new CategoryAxis();
        NumberAxis y = new NumberAxis();
        x.setLabel("Значение");
        y.setLabel("Количество");
        BarChart<String, Number> barChart = new BarChart<>(x, y);
        x.setTickLabelRotation(90);


        XYChart.Series<String, Number> ds = new XYChart.Series();
        for (int i = 0; i < sequenceHashMap.size(); i++) {
            ds.getData().add(new XYChart.Data<>(values.get(i).toString(), 0));
        }
        barChart.getData().add(ds);

        Timeline timeline = new Timeline();
        timeline.getKeyFrames().add(new KeyFrame(Duration.millis(1), new EventHandler<>() {
            int i = 0;

            @Override
            public void handle(ActionEvent actionEvent) {
                int value;
                if (i < sequence.size()) {
                    value = sequence.get(i);
                    ObservableList<XYChart.Series<String, Number>> series = barChart.getData();
                    for (XYChart.Data<String, Number> data : series.get(0).getData()) {
                        if (parseInt(data.getXValue()) == value) {
                            Number randomValue = data.getYValue().doubleValue() + 1;
                            data.setYValue(randomValue);
                            ds.setName(Integer.toString(value));
                            i++;
                        }
                    }
                } else {
                    timeline.stop();
                }
            }
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.setAutoReverse(true);
        timeline.play();

        VBox vBox = new VBox(barChart);
        Scene scene = new Scene(vBox, 500, 200);
        Stage primaryStage = new Stage();
        primaryStage.setScene(scene);
        primaryStage.setHeight(300);
        primaryStage.setWidth(400);
        primaryStage.show();
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