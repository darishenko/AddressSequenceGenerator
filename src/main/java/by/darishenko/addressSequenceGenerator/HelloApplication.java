package by.darishenko.addressSequenceGenerator;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {

    public static Stage istage;

    public static void main(String[] args) {
        launch();

        //test();
    }

//    public static List<String> test() {
//        List<String> fileLines = null;
//        try {
//            fileLines = FileWorker.readFileLines(new File("C:\\Users\\Darishenko\\Darishenko\\UNIVERSITY\\5 Semester\\CourseProject\\test.txt"));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        List<Integer> initialState = BinaryConverter.convertBinaryStringsToDigits(fileLines);
//        System.out.println(Arrays.toString(initialState.toArray()));
//
//        SwitchingSequenceGenerator swg = new SwitchingSequenceGenerator(4);
//        //List<Integer> T = swg.generateSequence();
//        //System.out.println(Arrays.toString(T.toArray()));
//
//
//        MainGenerator mainGenerator = new MainGenerator(4);
//        List<Integer> result = null;
//        try {
//            result = mainGenerator.generateSequence(initialState);
//        } catch (MyException e) {
//            showWarningMessage("Warning", "Файл не сохранен", "Повторите попытку");
//
//        }
//        System.out.println(Arrays.toString(result.toArray()));
//        System.out.println(Arrays.toString(BinaryConverter.convertDigitsToBinaryStrings(result, 3).toArray()));
//
//        try {
//            FileWorker.writeToFile(new File("C:\\Users\\Darishenko\\Darishenko\\UNIVERSITY\\5 Semester\\CourseProject\\result.txt"), BinaryConverter.convertDigitsToBinaryStrings(result, 4));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return BinaryConverter.convertDigitsToBinaryStrings(result, 3);
//    }

    @Override
    public void start(Stage stage) throws IOException {
        istage = stage;
        //stage.setMaximized(true);
        //stage.setResizable(false);
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        stage.setTitle("Генератор адресных последовательностей");
        stage.setScene(scene);
        stage.show();
    }
}