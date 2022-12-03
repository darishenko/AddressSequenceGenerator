package by.darishenko.addressSequenceGenerator.exception;

public class MyException extends Exception {

    private final String messageCorrection;
    private final String messageAdvice;

    public MyException(String message, String messageCorrection, String messageAdvice) {
        super(message);
        this.messageCorrection = messageCorrection;
        this.messageAdvice = messageAdvice;
    }

    public String getMessageCorrection() {
        return messageCorrection;
    }

    public String getMessageAdvice() {
        return messageAdvice;
    }

}
