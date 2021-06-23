package me.negroni.remotevehicle.controller.api.packet;

import java.util.*;
import java.util.function.Consumer;

public class PacketProcessor {

    private final Map<PacketType, List<Consumer<PacketContainer>>> callbacks = new HashMap<>();

    public PacketProcessor() {
        Arrays.stream(PacketType.values()).forEach(p -> callbacks.put(p, new ArrayList<>()));
    }

    public void registerCallback(PacketType packetType, Consumer<PacketContainer> lambda) {
        callbacks.get(packetType).add(lambda);
    }

    public void acceptPacket(PacketContainer packetContainer) {
        callbacks.get(packetContainer.getPacketType()).forEach(c -> c.accept(packetContainer));
    }
}
