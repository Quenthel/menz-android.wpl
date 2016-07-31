package com.example.wizardry.wPlayer.Helpers.mp3agic;

public class InvalidDataException extends BaseException {

    private static final long serialVersionUID = 1L;

    public InvalidDataException() {
        super();
    }

    public InvalidDataException(String message) {
        super(message);
    }

    public InvalidDataException(String message, Throwable cause) {
        super(message, cause);
    }
}
