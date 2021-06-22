package me.negroni.remotevehicle.controller.api.exceptions;

public class ClientNotConnectedException extends RuntimeException {

    public ClientNotConnectedException(String message) {
        super(message);
    }
}
