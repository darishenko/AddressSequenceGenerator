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

import static java.lang.Integer.parseInt;

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
            System.out.println(generatedAddressSequence);


            createGistogramm(generatedAddressSequence);
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

    private void createGistogramm(List<Integer> sequence) {
        HashMap<Integer, Integer> sequenceHashMap = new HashMap<>();
        for (int elem : sequence) {
            if (!sequenceHashMap.containsKey(elem)) {
                sequenceHashMap.put(elem, 1);
            } else {
                sequenceHashMap.put(elem, sequenceHashMap.get(elem) + 1);
            }
        }
        ArrayList<Integer> valueTimes = new ArrayList<>(sequenceHashMap.values());
        ArrayList<Integer> value = new ArrayList<>(sequenceHashMap.keySet());
        Collections.sort(valueTimes);
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
            ds.getData().add(new XYChart.Data<>(value.get(i).toString(), 0));
        }
        barChart.getData().add(ds);

        Timeline timeline = new Timeline();
        timeline.getKeyFrames().add(new KeyFrame(Duration.millis(500), new EventHandler<>() {
            int i = 0;

            @Override
            public void handle(ActionEvent actionEvent) {
                int value;
                if (i < sequence.size()) {
                    value = sequence.get(i);
                    ObservableList<XYChart.Series<String, Number>> series = barChart.getData();
                    for (XYChart.Data<String, Number> data : series.get(0).getData()) {
                        if (parseInt(data.getXValue()) == value) {
                            Number randomValue = data.getYValue().doubleValue() + (1);
                            data.setYValue(randomValue);
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


        });
        ta_generatingMatrix.textProperty().addListener(validationGeneratingSequenceListener);
        tf_initialState.textProperty().addListener(validationGeneratingSequenceListener);

        chMI_writeGenerateMatrixToTextArea.selectedProperty().addListener((observableValue, aBoolean, t1) -> {
            b_generateSequence.setDisable(!aBoolean);
        });

    }
}