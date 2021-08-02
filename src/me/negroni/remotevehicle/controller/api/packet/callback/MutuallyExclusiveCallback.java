package me.negroni.remotevehicle.controller.api.packet.callback;

import me.negroni.remotevehicle.controller.api.packet.PacketContainer;
import me.negroni.remotevehicle.controller.api.packet.PacketType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class MutuallyExclusiveCallback extends PacketCallback {

    private final Map<PacketType, List<Consumer<PacketContainer>>> callbacks;

    public MutuallyExclusiveCallback(Map<PacketType, List<Consumer<PacketContainer>>> callbacks) {
        super();
        this.callbacks = callbacks;
    }

    public MutuallyExclusiveCallback(Map<PacketType, List<Consumer<PacketContainer>>> callbacks, int uses) {
        super(uses);
        this.callbacks = callbacks;
    }

    public MutuallyExclusiveCallback() {
        super();
        this.callbacks = new HashMap<>();
    }

    public MutuallyExclusiveCallback(int uses) {
        super(uses);
        this.callbacks = new HashMap<>();
    }

    public void addCallback(PacketType packetType, Consumer<PacketContainer> consumer) {
        if (!callbacks.containsKey(packetType))
            callbacks.put(packetType, new ArrayList<>());

        callbacks.get(packetType).add(consumer);
    }

    public boolean containsCallback(PacketType packetType) {
        return callbacks.containsKey(packetType);
    }

    @Override
    public void acceptPacket(PacketContainer pc) {
        callbacks.get(pc.getPacketType()).forEach(c -> c.accept(pc));
    }
}
