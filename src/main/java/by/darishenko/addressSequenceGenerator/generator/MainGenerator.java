package by.darishenko.addressSequenceGenerator.generator;

import by.darishenko.addressSequenceGenerator.exception.MyException;

import java.util.ArrayList;
import java.util.List;

public class MainGenerator {

    private int length;
    private int initialState;

    public MainGenerator(int length, int initialState) {
        this.length = length;
        this.initialState = initialState;
    }

    public MainGenerator(int length) {
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

    public void setInitialState(int initialState) {
        this.initialState = initialState;
    }

    public List<Integer> generateSequence(List<Integer> generatingMatrix) throws MyException {
        List<Integer> result = new ArrayList<>();
        SwitchingSequenceGenerator switchingSequenceGenerator = new SwitchingSequenceGenerator(length);
        List<Integer> switchMatrix = switchingSequenceGenerator.generateSequence();
        int currState = initialState;

        result.add(currState);
        for (int i = 0; i < Math.pow(2, length) - 1; i++) {
            currState = currState ^ generatingMatrix.get(switchMatrix.get(i));
            result.add(currState);
        }

        return result;
    }
}
