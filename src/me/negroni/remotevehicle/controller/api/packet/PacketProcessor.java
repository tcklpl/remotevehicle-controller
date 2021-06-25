package me.negroni.remotevehicle.controller.api.packet;

import java.util.*;
import java.util.function.Consumer;

public class PacketProcessor {

    private final Map<PacketType, List<PacketCallback>> callbacks = new HashMap<>();

    public PacketProcessor() {
        Arrays.stream(PacketType.values()).forEach(p -> callbacks.put(p, new ArrayList<>()));
    }

    public void registerCallback(PacketType packetType, Consumer<PacketContainer> lambda) {
        callbacks.get(packetType).add(new PacketCallback(lambda));
    }

    public void registerCallbackForNUses(PacketType packetType, int uses, Consumer<PacketContainer> lambda) {
        callbacks.get(packetType).add(new PacketCallback(lambda, uses));
    }

    public void acceptPacket(PacketContainer packetContainer) {
        System.out.println(packetContainer.getPacketType());

        Set<PacketCallback> toRemove = new HashSet<>();

        callbacks.get(packetContainer.getPacketType()).forEach(c -> {
            c.getCallback().accept(packetContainer);
            if (c.isLimited()) {
                c.decreaseUsesLeft();
                if (c.getUsesLeft() <= 0) {
                    toRemove.add(c);
                }
            }
        });

        callbacks.get(packetContainer.getPacketType()).removeAll(toRemove);
    }
}
