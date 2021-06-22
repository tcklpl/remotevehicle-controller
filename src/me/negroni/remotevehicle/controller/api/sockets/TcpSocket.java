package me.negroni.remotevehicle.controller.api.sockets;

import me.negroni.remotevehicle.controller.api.packet.PacketContainer;
import me.negroni.remotevehicle.controller.api.packet.PacketProcessor;
import me.negroni.remotevehicle.controller.api.packet.PacketType;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class TcpSocket implements Runnable {

    private Socket socket;
    DataInputStream inputStream;
    BufferedReader bufferedReader;
    private boolean shouldRun;
    private byte[] outBuffer;

    private final PacketProcessor packetProcessor;

    private int nextBytesToRead;
    private PacketContainer packetToContinueBytes;

    public TcpSocket(PacketProcessor packetProcessor, InetAddress address, int port) {
        this.packetProcessor = packetProcessor;
        try {
            socket = new Socket(address, port);
            socket.setTcpNoDelay(true);
            inputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            shouldRun = true;
            System.out.println("Successfully created tcp socket to " + address.getHostAddress() + ":" + port);
        } catch (IOException e) {
            System.err.println("Failed to create tcp socket to " + address.getHostAddress() + ":" + port);
            e.printStackTrace();
        }
    }

    public void sendPacket(PacketType type, String... optionalInfo) {
        sendMessage(type.getCode() + String.join("", optionalInfo));
    }

    private void sendMessage(String msg) {
        outBuffer = msg.getBytes(StandardCharsets.ISO_8859_1);
    }

    public void setNextBytesToRead(int nextBytesToRead, PacketContainer toWhere) {
        this.nextBytesToRead = nextBytesToRead;
        this.packetToContinueBytes = toWhere;
    }

    @Override
    public void run() {
        while (shouldRun) {
            // write is there is anything to write
            if (outBuffer != null) {
                try {
                    socket.getOutputStream().write(outBuffer);
                    socket.getOutputStream().flush();
                    outBuffer = null;

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

                    if (nextBytesToRead > 0) {
                        byte[] continuation = socket.getInputStream().readNBytes(nextBytesToRead);
                        packetToContinueBytes.lateAddBytes(continuation);
                        packetProcessor.acceptPacket(packetToContinueBytes);
                        nextBytesToRead = 0;
                        packetToContinueBytes = null;
                    }
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
