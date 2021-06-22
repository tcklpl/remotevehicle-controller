package me.negroni.remotevehicle.controller.api;

import me.negroni.remotevehicle.controller.api.exceptions.ClientNotConnectedException;
import me.negroni.remotevehicle.controller.api.exceptions.NotAClientPacketException;
import me.negroni.remotevehicle.controller.api.packet.PacketContainer;
import me.negroni.remotevehicle.controller.api.packet.PacketProcessor;
import me.negroni.remotevehicle.controller.api.packet.PacketType;
import me.negroni.remotevehicle.controller.api.sockets.TcpSocket;
import me.negroni.remotevehicle.controller.api.sockets.UdpSocket;

import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

public class VehicleCommunication {

    private final PacketProcessor packetProcessor = new PacketProcessor();
    private final UdpSocket udpSocket = new UdpSocket(packetProcessor, 6887);
    private TcpSocket tcpSocket;
    private boolean connected = false;

    public VehicleCommunication() {
        registerDefaultCallbacks();
    }

    public void startThreads() {
        new Thread(udpSocket).start();
    }

    private void registerDefaultCallbacks() {
        packetProcessor.registerCallback(PacketType.PACKET_SERVER_BROADCASTING, c -> {
            if (tcpSocket != null) return;
            tcpSocket = new TcpSocket(packetProcessor, c.getRemoteAddress(), 6887);
            new Thread(tcpSocket).start();
            tcpSocket.sendPacket(PacketType.PACKET_REQUEST_CONNECTION);
        });

        packetProcessor.registerCallback(PacketType.PACKET_ACCEPTED_CONNECTION, c -> {
            connected = true;
            System.out.println("Connected!");
        });

        packetProcessor.registerCallback(PacketType.PACKET_HEARTBEAT, c -> {
            tcpSocket.sendPacket(PacketType.PACKET_HEARTBEAT);
        });

        packetProcessor.registerCallback(PacketType.PACKET_CAMERA_IMAGE, c -> {
            if (!c.hasLateBytes()) {
                try {
                    String original = new String(c.getWholePacket(), StandardCharsets.ISO_8859_1).substring(4, 10).trim();
                    int len = Integer.parseInt(original);
                    tcpSocket.setNextBytesToRead(len, c);
                } catch (Exception e) {
                    System.err.println("error when trying to get image");
                    e.printStackTrace();
                }
            }
        });
    }

    public void registerCustomCallback(PacketType packetType, Consumer<PacketContainer> lambda) {
        packetProcessor.registerCallback(packetType, lambda);
    }

    public void sendPacket(PacketType packetType, String... extraInfo) {
        if (!connected) throw new ClientNotConnectedException("cannot send packets while tcp client is not yet connected");
        if (packetType.getSender() != PacketType.PackerSender.CLIENT && packetType.getSender() != PacketType.PackerSender.ANY)
            throw new NotAClientPacketException("cannot send a server packet");

        tcpSocket.sendPacket(packetType, extraInfo);
    }
}
