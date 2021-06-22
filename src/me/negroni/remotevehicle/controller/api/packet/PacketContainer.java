package me.negroni.remotevehicle.controller.api.packet;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

public class PacketContainer {

    private DatagramPacket udpPacket;
    private final PacketContainerMode mode;
    private final PacketType packetType;
    private InetAddress remoteAddress;
    private byte[] rawData, lateBytes;

    public PacketContainer(DatagramPacket udpPacket) {
        this.udpPacket = udpPacket;
        this.mode = PacketContainerMode.UDP_MODE;
        String first4chars = new String(udpPacket.getData(), 0, 4, StandardCharsets.US_ASCII);
        this.packetType = Stream.of(PacketType.values()).filter(pt -> pt.getCode().equals(first4chars)).findFirst().orElse(PacketType.UNKNOWN_PACKET);
    }

    public PacketContainer(byte[] rawData, InetAddress remoteAddress) {
        this.remoteAddress = remoteAddress;
        this.rawData = rawData;
        this.mode = PacketContainerMode.TCP_MODE;
        String first4chars = new String(rawData, 0, 4, StandardCharsets.US_ASCII);
        this.packetType = Stream.of(PacketType.values()).filter(pt -> pt.getCode().equals(first4chars)).findFirst().orElse(PacketType.UNKNOWN_PACKET);
    }

    public PacketType getPacketType() {
        return packetType;
    }

    public byte[] getWholePacket() {
        return mode == PacketContainerMode.TCP_MODE ? rawData : udpPacket.getData();
    }

    public byte[] getData() {
        byte[] raw = mode == PacketContainerMode.TCP_MODE ? rawData : udpPacket.getData();
        Byte[] objectArray = Stream.of(raw).skip(4).toArray(Byte[]::new);
        byte[] outSkipped = new byte[objectArray.length];
        // need to do this because we need to return the primitive type byte, and not its wrapper
        for (int i = 0; i < objectArray.length; i++) {
            outSkipped[i] = objectArray[i];
        }
        return outSkipped;
    }

    public InetAddress getRemoteAddress() {
        return mode == PacketContainerMode.TCP_MODE ? remoteAddress : udpPacket.getAddress();
    }

    public void lateAddBytes(byte[] data) {
        this.lateBytes = data;
    }

    public boolean hasLateBytes() {
        return lateBytes != null;
    }

    public byte[] getLateBytes() {
        return lateBytes;
    }

    private enum PacketContainerMode {
        TCP_MODE, UDP_MODE
    }
}
