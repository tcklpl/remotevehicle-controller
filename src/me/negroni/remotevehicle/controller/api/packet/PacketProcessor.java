package me.negroni.remotevehicle.controller.api.packet;

import me.negroni.remotevehicle.controller.api.packet.callback.MutuallyExclusiveCallback;
import me.negroni.remotevehicle.controller.api.packet.callback.NormalCallback;
import me.negroni.remotevehicle.controller.api.packet.callback.PacketCallback;

import java.util.*;
import java.util.function.Consumer;

public class PacketProcessor {

    private final Map<PacketType, List<PacketCallback>> callbacks = new HashMap<>();
    private final List<MutuallyExclusiveCallback> exclusiveCallbacks = new ArrayList<>();

    public PacketProcessor() {
        Arrays.stream(PacketType.values()).forEach(p -> callbacks.put(p, new ArrayList<>()));
    }

    public void registerCallback(PacketType packetType, Consumer<PacketContainer> lambda) {
        callbacks.get(packetType).add(new NormalCallback(lambda));
    }

    public void registerCallbackForNUses(PacketType packetType, int uses, Consumer<PacketContainer> lambda) {
        callbacks.get(packetType).add(new NormalCallback(lambda, uses));
    }

    public void registerMutuallyExclusiveCallback(MutuallyExclusiveCallback callback) {
        exclusiveCallbacks.add(callback);
    }

    public void acceptPacket(PacketContainer packetContainer) {
        System.out.println(packetContainer.getPacketType());

        Set<PacketCallback> toRemove = new HashSet<>();

        callbacks.get(packetContainer.getPacketType()).forEach(c -> {
            c.acceptPacket(packetContainer);
            if (c.isLimited()) {
                c.decreaseUsesLeft();
                if (c.getUsesLeft() <= 0) {
                    toRemove.add(c);
                }
            }
        });

        callbacks.get(packetContainer.getPacketType()).removeAll(toRemove);

        Set<MutuallyExclusiveCallback> exclusivesToRemove = new HashSet<>();

        if (!exclusiveCallbacks.isEmpty()) {
            exclusiveCallbacks.stream().filter(c -> c.containsCallback(packetContainer.getPacketType())).forEach(c -> {
                c.acceptPacket(packetContainer);
                if (c.isLimited()) {
                    c.decreaseUsesLeft();
                    if (c.getUsesLeft() <= 0)
                        exclusivesToRemove.add(c);
                }
            });
        }

        exclusiveCallbacks.removeAll(exclusivesToRemove);
    }
}
