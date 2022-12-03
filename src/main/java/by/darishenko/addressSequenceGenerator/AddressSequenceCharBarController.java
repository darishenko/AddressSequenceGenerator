package by.darishenko.addressSequenceGenerator;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.MenuBar;
import javafx.scene.image.WritableImage;
import javafx.util.Duration;

import java.io.File;
import java.net.URL;
import java.util.*;

import static java.lang.Integer.parseInt;

public class AddressSequenceCharBarController {

    @FXML
    public BarChart<String, Number> BarChart_AddressSequence;
    @FXML
    private ResourceBundle resources;
    @FXML
    private URL location;
    @FXML
    private MenuBar MenuBar;

    public void setBarChart_AddressSequence(List<Integer> sequence, int animationSpeed) {
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

        BarChart_AddressSequence.getXAxis().setTickLabelRotation(90);


        XYChart.Series<String, Number> ds = new XYChart.Series();
        for (int i = 0; i < sequenceHashMap.size(); i++) {
            ds.getData().add(new XYChart.Data<>(values.get(i).toString(), 0));
        }
        BarChart_AddressSequence.getData().add(ds);

        Timeline timeline = new Timeline();
        timeline.getKeyFrames().add(new KeyFrame(Duration.millis(animationSpeed), new EventHandler<>() {
            int i = 0;

            @Override
            public void handle(ActionEvent actionEvent) {
                int value;
                if (i < sequence.size()) {
                    value = sequence.get(i);
                    ObservableList<XYChart.Series<String, Number>> series = BarChart_AddressSequence.getData();
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
    }

    private void saveAsPng(Node node, String fileName) {
        saveAsPng(node, fileName, new SnapshotParameters());
    }

    private void saveAsPng(Node node, String fileName, SnapshotParameters ssp) {
        WritableImage image = node.snapshot(ssp, null);
        File file = new File(fileName + ".png");
    }

    @FXML
    void initialize() {

    }

}

