package by.darishenko.addressSequenceGenerator;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class SetAnimationSpeedController {
    private HelloController parent;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button b_setAnimationSpeed;
    @FXML
    private TextField tf_animationSpeed;
    @FXML
    public Label lb_currentAnimationSpeed;
    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setParent(HelloController parent) {
        this.parent = parent;
    }

    public void setCurrentAnimationSpeed(int animationSpeed){
        lb_currentAnimationSpeed.setText("Текущее значение " + animationSpeed);
    }

    @FXML
    public void setAnimationSpeed() {
        int animationSpeed = Integer.parseInt(tf_animationSpeed.getText());
        parent.setAnimationSpeed(animationSpeed);
        stage.close();
    }

    @FXML
    void initialize() {

        ChangeListener<String> validateAnimationSpeed = ((observable, oldValue, newValue) -> {
            int errorValidationCounter = 0;
            int value = 0;
            try {
                value = Integer.parseInt(newValue);
            } catch (NumberFormatException ex) {

            }
            if (value <= 0 || value > 60000) {
                tf_animationSpeed.setStyle("-fx-border-color: red ; fx-border-width: 2px");
                errorValidationCounter++;
            } else {
                tf_animationSpeed.setStyle(null);
            }

            b_setAnimationSpeed.setDisable(errorValidationCounter != 0);
        });

        b_setAnimationSpeed.setDisable(true);
        tf_animationSpeed.textProperty().addListener(validateAnimationSpeed);
    }

}
