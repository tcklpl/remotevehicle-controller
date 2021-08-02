package me.negroni.remotevehicle.controller.api.packet.callback;

import me.negroni.remotevehicle.controller.api.packet.PacketContainer;

public abstract class PacketCallback {

    private final boolean limited;
    private int usesLeft;

    public PacketCallback() {
        this.limited = false;
    }

    public PacketCallback(int usesLeft) {
        this.limited = true;
        this.usesLeft = usesLeft;
    }

    /**
     * Void to be defined by its children, used to accept given packet.
     * @param pc the packet to trigger the callbacks.
     */
    public abstract void acceptPacket(PacketContainer pc);

    /**
     * Returns the number of usages left (used when dealing with a limited callback).
     * @return the number of uses left for this callback.
     */
    public int getUsesLeft() {
        return usesLeft;
    }

    /**
     * Decreases the number of uses left for this callback (used when dealing with a limited callback).
     */
    public void decreaseUsesLeft() {
        usesLeft--;
    }

    /**
     * To check if a callback is limited, that is, has a limited number of executions before being excluded.
     * @return if the callback has a limited number of executions.
     */
    public boolean isLimited() {
        return limited;
    }
}
