package by.darishenko.addressSequenceGenerator.math;

public class ExtraMath {
    private static final double log2WithBaseExp = Math.log(2);

    public static int log2(int N) {
        return (int) (Math.log(N) / log2WithBaseExp);
    }
}