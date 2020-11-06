package Exceptions;

public class WrongColorException extends RuntimeException {
    public WrongColorException(String message) {
        this.message = message;
    }

    public WrongColorException() {
        message = "Color must be value between 0-255 for integer and value between 0-1 for floating point";
    }

    private String message;

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "Color must be value between 0-255 for integer and value between 0-1 for floating point";
    }
}
