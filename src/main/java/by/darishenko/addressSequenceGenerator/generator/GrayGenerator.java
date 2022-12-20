package by.darishenko.addressSequenceGenerator.generator;

import by.darishenko.addressSequenceGenerator.exception.MyException;

import java.util.ArrayList;
import java.util.List;

public class GrayGenerator {
    //#TODO maybe change the rec algorithm to the algorithm without it
    public static List<Integer> generateSequence(int n) throws MyException {
        try {
            if (n == 0) {
                List<Integer> result = new ArrayList<>();
                result.add(0);
                return result;
            }

            List<Integer> result = generateSequence(n - 1);
            int numToAdd = 1 << (n - 1);

            for (int i = result.size() - 1; i >= 0; i--) {
                result.add(numToAdd + result.get(i));
            }
            return result;
        } catch (StackOverflowError e) {
            throw new MyException("Ошибка", "При генерации последовательности возникла ошибка", "Введите порождающую матрицу меньшей длины");
        }
    }

}
