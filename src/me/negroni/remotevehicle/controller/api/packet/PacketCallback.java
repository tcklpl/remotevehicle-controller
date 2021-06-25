package me.negroni.remotevehicle.controller.api.packet;

import java.util.function.Consumer;

public class PacketCallback {

    private final Consumer<PacketContainer> callback;
    private final boolean limited;
    private int usesLeft;

    public PacketCallback(Consumer<PacketContainer> callback) {
        this.callback = callback;
        this.limited = false;
    }

    public PacketCallback(Consumer<PacketContainer> callback, int usesLeft) {
        this.callback = callback;
        this.limited = true;
        this.usesLeft = usesLeft;
    }

    public Consumer<PacketContainer> getCallback() {
        return callback;
    }

    public int getUsesLeft() {
        return usesLeft;
    }

    public void decreaseUsesLeft() {
        usesLeft--;
    }

    public boolean isLimited() {
        return limited;
    }
}
