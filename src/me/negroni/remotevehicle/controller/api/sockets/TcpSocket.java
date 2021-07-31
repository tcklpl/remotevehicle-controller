package me.negroni.remotevehicle.controller.api.sockets;

import me.negroni.remotevehicle.controller.api.packet.PacketContainer;
import me.negroni.remotevehicle.controller.api.packet.PacketProcessor;

import java.net.InetAddress;

public class TcpSocket extends GenericTcpSocket implements Runnable {

    private final PacketProcessor packetProcessor;

    public TcpSocket(PacketProcessor packetProcessor, InetAddress address, int port) {
        super(address, port);
        this.packetProcessor = packetProcessor;
    }

    @Override
    public void run() {
        while (shouldRun) {
            writeIfTheresAnything();

            byte[] read = read(100000);
            if (read != null) {
                PacketContainer test = new PacketContainer(read, getAddress());
                packetProcessor.acceptPacket(test);
            }
        }
    }
}
