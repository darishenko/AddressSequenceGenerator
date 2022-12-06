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
import javafx.scene.control.Button;
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
    @FXML
    private Button b_startAnimationAgain;
    @FXML
    private Button b_stopAnimation;
    private Timeline timeline;
    private boolean isStopped;
    private int uniqueSequenceValuesCount;
    private ArrayList<Integer> sequenceValues;
    public XYChart.Series<String, Number> dataSeries = new XYChart.Series();

    public void setAnimationSpeed(int animationSpeed) {
        this.animationSpeed = animationSpeed;
    }

    private int animationSpeed;

    public void setAddressSequence(List<Integer> addressSequence) {
        this.addressSequence = addressSequence;
    }

    private  List<Integer> addressSequence;

    public void startAnimationAtFirst(){
        HashMap<Integer, Integer> sequenceHashMap = createHashMap();
        uniqueSequenceValuesCount = sequenceHashMap.size();

        sequenceValues = new ArrayList<>(sequenceHashMap.keySet());
        Collections.sort(sequenceValues);

        animateBarChart();
    }

    private HashMap<Integer, Integer> createHashMap(){
        HashMap<Integer, Integer> sequenceHashMap = new HashMap<>();
        for (int elem : addressSequence) {
            if (!sequenceHashMap.containsKey(elem)) {
                sequenceHashMap.put(elem, 1);
            } else {
                sequenceHashMap.put(elem, sequenceHashMap.get(elem) + 1);
            }
        }
        return sequenceHashMap;
    }

    private void animateBarChart() {

        XYChart.Series<String, Number> dataSeries = new XYChart.Series();;
        for (int i = 0; i < uniqueSequenceValuesCount; i++) {
            dataSeries.getData().add(new XYChart.Data<>(sequenceValues.get(i).toString(), 0));
        }
        BarChart_AddressSequence.getData().add(dataSeries);

        BarChart_AddressSequence.setAnimated(true);
        timeline = new Timeline();
        timeline.getKeyFrames().add(new KeyFrame(Duration.millis(animationSpeed), new EventHandler<>() {

            int i = 0;
            @Override
            public void handle(ActionEvent actionEvent) {
                int value;
                if (i < addressSequence.size()) {
                    value = addressSequence.get(i);
                    ObservableList<XYChart.Series<String, Number>> series = BarChart_AddressSequence.getData();
                    for (XYChart.Data<String, Number> data : series.get(0).getData()) {
                        if (parseInt(data.getXValue()) == value) {
                            Number randomValue = data.getYValue().doubleValue() + 1;
                            data.setYValue(randomValue);
                            dataSeries.setName(Integer.toString(value));
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

    @FXML
    private void pauseAnimation(){
        if(!isStopped){
            timeline.pause();
            b_stopAnimation.setText("Продолжить");
            isStopped = true;
        }else{
            timeline.play();
            b_stopAnimation.setText("Приостановить");
            isStopped = false;
        }
    }

    @FXML
    private void startAnimationAgain() throws InterruptedException {
        timeline.stop();
        BarChart_AddressSequence.setAnimated(false);
        BarChart_AddressSequence.getData().clear();
        BarChart_AddressSequence.layout();

        animateBarChart();
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

