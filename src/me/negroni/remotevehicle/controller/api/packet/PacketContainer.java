package me.negroni.remotevehicle.controller.api.packet;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class PacketContainer {

    private DatagramPacket udpPacket;
    private final PacketContainerMode mode;
    private final PacketType packetType;
    private InetAddress remoteAddress;
    private final byte[] rawData;

    public PacketContainer(DatagramPacket udpPacket) {
        this.udpPacket = udpPacket;
        this.mode = PacketContainerMode.UDP_MODE;
        String first4chars = new String(udpPacket.getData(), 0, 4, StandardCharsets.US_ASCII);
        this.packetType = Stream.of(PacketType.values()).filter(pt -> pt.getCode().equals(first4chars)).findFirst().orElse(PacketType.UNKNOWN_PACKET);
        this.rawData = udpPacket.getData();
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
        return rawData;
    }

    public byte[] getData() {
        Byte[] objectArray = Stream.of(rawData).skip(4).toArray(Byte[]::new);
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

    private enum PacketContainerMode {
        TCP_MODE, UDP_MODE
    }
}
