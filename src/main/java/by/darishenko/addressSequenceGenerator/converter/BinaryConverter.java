package by.darishenko.addressSequenceGenerator.converter;

import by.darishenko.addressSequenceGenerator.exception.MyException;

import java.util.ArrayList;
import java.util.List;

public class BinaryConverter {

    public static int convertBinaryStringToDigit(String string) throws MyException {
        try {
            return Integer.parseInt(string, 2);
        }catch (NumberFormatException e){
            throw new MyException("Warning", "Ошибка генерации адресной последовательности", "Адресная " +
                    "последовательность содержит недопустимые символы");
        }
    }

    public static List<Integer> convertBinaryStringsToDigits(List<String> strings) throws MyException{
        List<Integer> result = new ArrayList<>();
        for (String string : strings) {
            result.add(convertBinaryStringToDigit(string));
        }
        return result;
    }

    public static String convertDigitToBinaryString(int digit, int length) {
        String result = Integer.toBinaryString(digit);
        if (result.length() < length) {
            result = String.format("%0" + (length - result.length()) + "d%s", 0, result);
        }
        return result;
    }

    public static List<String> convertDigitsToBinaryStrings(List<Integer> digits, int length) {
        List<String> result = new ArrayList<>();
        for (Integer digit : digits) {
            result.add(convertDigitToBinaryString(digit, length));
        }
        return result;
    }
}
