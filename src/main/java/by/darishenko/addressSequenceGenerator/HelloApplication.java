package by.darishenko.addressSequenceGenerator;

import by.darishenko.addressSequenceGenerator.generator.MainGenerator;
import by.darishenko.addressSequenceGenerator.generator.SwitchingSequenceGenerator;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class HelloApplication extends Application {
    public static void main(String[] args) {
        launch();

        test();
    }

    private static void test() {
        List<String> fileLines = null;
        try {
            fileLines = FileWorker.readFileLines(new File("C:\\Users\\Darishenko\\Darishenko\\UNIVERSITY\\5 Semester\\CourseProject\\test.txt"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<Integer> initialState = BinaryConverter.convertBinaryStringsToDigits(fileLines);
        System.out.println(Arrays.toString(initialState.toArray()));

        SwitchingSequenceGenerator swg = new SwitchingSequenceGenerator(4);
        List<Integer> T = swg.generateSequence();
        System.out.println(Arrays.toString(T.toArray()));


        MainGenerator mainGenerator = new MainGenerator(4);
        List<Integer> result = mainGenerator.generateSequence(initialState);
        System.out.println(Arrays.toString(result.toArray()));
        System.out.println(Arrays.toString(BinaryConverter.convertDigitsToBinaryStrings(result, 3).toArray()));

        try {
            FileWorker.writeToFile(new File("C:\\Users\\Darishenko\\Darishenko\\UNIVERSITY\\5 Semester\\CourseProject\\result.txt"), BinaryConverter.convertDigitsToBinaryStrings(result, 4));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Генератор адресных последовательностей");
        stage.setScene(scene);
        stage.show();
    }
}