package me.negroni.remotevehicle.controller.api.sockets;

import me.negroni.remotevehicle.controller.api.packet.PacketContainer;
import me.negroni.remotevehicle.controller.api.packet.PacketProcessor;
import me.negroni.remotevehicle.controller.api.packet.PacketType;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.Queue;

public class TcpSocket implements Runnable {

    private Socket socket;
    private boolean shouldRun;
    private final Queue<byte[]> outQueue = new LinkedList<>();

    private final PacketProcessor packetProcessor;


    public TcpSocket(PacketProcessor packetProcessor, InetAddress address, int port) {
        this.packetProcessor = packetProcessor;
        try {
            socket = new Socket(address, port);
            socket.setTcpNoDelay(true);
            shouldRun = true;
            System.out.println("Successfully created tcp socket to " + address.getHostAddress() + ":" + port);
        } catch (IOException e) {
            System.err.println("Failed to create tcp socket to " + address.getHostAddress() + ":" + port);
            e.printStackTrace();
        }
    }

    public void sendPacket(PacketType type, String optionalInfo) {
        sendMessage(type.getCode() + (optionalInfo == null ? "" : optionalInfo));
    }

    private void sendMessage(String msg) {
        outQueue.add(msg.getBytes(StandardCharsets.ISO_8859_1));
    }

    @Override
    public void run() {
        while (shouldRun) {
            // write is there is anything to write
            if (!outQueue.isEmpty()) {
                try {
                    socket.getOutputStream().write(outQueue.remove());
                    socket.getOutputStream().flush();

                } catch (IOException e) {
                    System.err.println("Failed to write to tcp socket out stream");
                    e.printStackTrace();
                }
            }
            // read if there is anything to read
            try {
                byte[] inputData = new byte[100000];
                int result = socket.getInputStream().read(inputData, 0, socket.getInputStream().available());
                if (result > 0) {
                    PacketContainer test = new PacketContainer(inputData, socket.getInetAddress());
                    packetProcessor.acceptPacket(test);
                }
            } catch (IOException e) {
                System.err.println("Failed to read tcp socket in stream");
                e.printStackTrace();
            }
        }
    }

    public void stop() {
        this.shouldRun = false;
    }
}
