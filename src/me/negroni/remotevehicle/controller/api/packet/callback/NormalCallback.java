package me.negroni.remotevehicle.controller.api.packet.callback;

import me.negroni.remotevehicle.controller.api.packet.PacketContainer;

import java.util.function.Consumer;

public class NormalCallback extends PacketCallback {

    private final Consumer<PacketContainer> callback;

    public NormalCallback(Consumer<PacketContainer> callback) {
        super();
        this.callback = callback;
    }

    public NormalCallback(Consumer<PacketContainer> callback, int usesLeft) {
        super(usesLeft);
        this.callback = callback;
    }

    @Override
    public void acceptPacket(PacketContainer pc) {
        callback.accept(pc);
    }
}
