package by.darishenko.addressSequenceGenerator.generator;

import by.darishenko.addressSequenceGenerator.math.ExtraMath;

import java.util.ArrayList;
import java.util.List;

public class SwitchingSequenceGenerator {

    private final int initialState;
    private int length;

    public SwitchingSequenceGenerator(int length) {
        this.length = length;
        this.initialState = 0;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getInitialState() {
        return initialState;
    }

    public List<Integer> generateSequence() {
        List<Integer> grayCodes = GrayGenerator.generateSequence(length);
        List<Integer> result = new ArrayList<>();

        for (int i = 1; i < grayCodes.size(); i++) {
            result.add(ExtraMath.log2(grayCodes.get(i - 1) ^ grayCodes.get(i)));
        }
        return result;
    }

}
