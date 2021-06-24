package me.negroni.remotevehicle.controller.api;

import me.negroni.remotevehicle.controller.api.exceptions.ClientNotConnectedException;
import me.negroni.remotevehicle.controller.api.exceptions.NotAClientPacketException;
import me.negroni.remotevehicle.controller.api.packet.PacketContainer;
import me.negroni.remotevehicle.controller.api.packet.PacketProcessor;
import me.negroni.remotevehicle.controller.api.packet.PacketType;
import me.negroni.remotevehicle.controller.api.sockets.ImageTcpSocket;
import me.negroni.remotevehicle.controller.api.sockets.TcpSocket;
import me.negroni.remotevehicle.controller.api.sockets.UdpSocket;

import java.util.function.Consumer;

public class VehicleCommunication {

    private final PacketProcessor packetProcessor = new PacketProcessor();
    private final UdpSocket udpSocket = new UdpSocket(packetProcessor, 6888);
    private TcpSocket tcpSocket;
    private ImageTcpSocket imageTcpSocket;
    private boolean connected = false;
    private boolean shouldTryToConnect = false;

    public VehicleCommunication() {
        registerDefaultCallbacks();
    }

    public void startThreads() {
        new Thread(udpSocket).start();
    }

    private void registerDefaultCallbacks() {
        packetProcessor.registerCallback(PacketType.PACKET_SERVER_BROADCASTING, c -> {
            if (connected) return;
            if (!shouldTryToConnect) return;
            if (tcpSocket != null)
                tcpSocket.stop();
            if (imageTcpSocket != null)
                imageTcpSocket.stop();
            tcpSocket = new TcpSocket(packetProcessor, c.getRemoteAddress(), 6887);
            imageTcpSocket = new ImageTcpSocket(packetProcessor, c.getRemoteAddress(), 6889);
            new Thread(tcpSocket).start();
            new Thread(imageTcpSocket).start();
            tcpSocket.sendPacket(PacketType.PACKET_REQUEST_CONNECTION, null);
        });

        packetProcessor.registerCallback(PacketType.PACKET_ACCEPTED_CONNECTION, c -> {
            connected = true;
            System.out.println("Connected!");
        });

        packetProcessor.registerCallback(PacketType.PACKET_HEARTBEAT, c -> {
            sendPacket(PacketType.PACKET_HEARTBEAT);
        });

        packetProcessor.registerCallback(PacketType.PACKET_CONFIRM_CONNECTION_END, c -> {
            endConnection();
        });

        packetProcessor.registerCallback(PacketType.PACKET_FORCE_CONNECTION_END, c -> {
            endConnection();
        });
    }

    public void disconnect() {
        if (!connected) throw new ClientNotConnectedException("client is not yet connected");
        sendPacket(PacketType.PACKET_REQUEST_CONNECTION_END);
    }

    private void endConnection() {
        stopThreads();
        connected = false;
        shouldTryToConnect = false;
    }

    private void stopThreads() {
        tcpSocket.stop();
        imageTcpSocket.stop();
    }

    public void registerCustomCallback(PacketType packetType, Consumer<PacketContainer> lambda) {
        packetProcessor.registerCallback(packetType, lambda);
    }

    public void sendPacket(PacketType packetType) {
        if (!connected) throw new ClientNotConnectedException("cannot send packets while tcp client is not yet connected");
        if (packetType.getSender() != PacketType.PackerSender.CLIENT && packetType.getSender() != PacketType.PackerSender.ANY)
            throw new NotAClientPacketException("cannot send a server packet");

        tcpSocket.sendPacket(packetType, null);
    }

    public void sendPacket(PacketType packetType, String optionalInfo) {
        if (!connected) throw new ClientNotConnectedException("cannot send packets while tcp client is not yet connected");
        if (packetType.getSender() != PacketType.PackerSender.CLIENT && packetType.getSender() != PacketType.PackerSender.ANY)
            throw new NotAClientPacketException("cannot send a server packet");

        tcpSocket.sendPacket(packetType, optionalInfo);
    }

    public boolean isTryingToConnect() {
        return shouldTryToConnect;
    }

    public void setShouldTryToConnect(boolean shouldTryToConnect) {
        this.shouldTryToConnect = shouldTryToConnect;
    }

    public boolean isConnected() {
        return connected;
    }
}
