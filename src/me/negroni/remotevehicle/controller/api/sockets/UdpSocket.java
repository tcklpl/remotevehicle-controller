package me.negroni.remotevehicle.controller.api.sockets;

import me.negroni.remotevehicle.controller.api.packet.PacketContainer;
import me.negroni.remotevehicle.controller.api.packet.PacketProcessor;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class UdpSocket implements Runnable {

    private final byte[] receivedData;
    private DatagramSocket socket;
    private boolean shouldRun;
    private final PacketProcessor packetProcessor;

    public UdpSocket(PacketProcessor packetProcessor, int port) {
        this.receivedData = new byte[10];
        this.shouldRun = true;
        this.packetProcessor = packetProcessor;
        try {
            socket = new DatagramSocket(port);
            System.out.println("Listening udp on port " + port);
        } catch (SocketException e) {
            System.err.println("Failed to bind udp socket on port " + port);
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (shouldRun) {
            DatagramPacket packet = new DatagramPacket(receivedData, receivedData.length);
            try {
                socket.receive(packet);
                packetProcessor.acceptPacket(new PacketContainer(packet));
            } catch (IOException e) {
                System.err.println("UDP Socket failed to receive message");
                e.printStackTrace();
            }
        }
    }

    public void stop() {
        this.shouldRun = false;
    }
}
