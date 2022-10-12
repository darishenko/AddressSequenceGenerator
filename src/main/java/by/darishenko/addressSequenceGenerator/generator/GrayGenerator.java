package by.darishenko.addressSequenceGenerator.generator;

import java.util.ArrayList;
import java.util.List;

public class GrayGenerator {

    public static List<Integer> generateSequence(int n) {
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
    }

}
