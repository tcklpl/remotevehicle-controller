package me.negroni.remotevehicle.controller.api.exceptions;

public class NotAClientPacketException extends RuntimeException {

    public NotAClientPacketException(String message) {
        super(message);
    }
}
