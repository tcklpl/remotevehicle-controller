package me.negroni.remotevehicle.controller.api.sockets;

import me.negroni.remotevehicle.controller.api.packet.PacketType;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.Queue;

public abstract class GenericTcpSocket {

    private Socket socket;
    protected boolean shouldRun;
    private final Queue<byte[]> outQueue = new LinkedList<>();

    public GenericTcpSocket(InetAddress address, int port) {
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

    protected void writeIfTheresAnything() {
        if (!outQueue.isEmpty())
            write(outQueue.remove());
    }

    protected void write(byte[] info) {
        try {
            socket.getOutputStream().write(info);
            socket.getOutputStream().flush();
        } catch (IOException e) {
            System.err.println("Failed to write to tcp socket out stream");
            e.printStackTrace();
        }
    }

    protected byte[] read(int bufferSize) {
        try {
            byte[] inputData = new byte[bufferSize];
            int result = socket.getInputStream().read(inputData, 0, socket.getInputStream().available());
            if (result > 0) {
                return inputData;
            }
        } catch (IndexOutOfBoundsException e) {
            System.err.println("Index out of bounds, skipping bytes...");
            try {
                socket.getInputStream().skipNBytes(socket.getInputStream().available());
            } catch (IOException ex) {
                System.err.println("Failed to skip bytes");
                ex.printStackTrace();
            }
        } catch (IOException e) {
            System.err.println("Failed to read tcp socket in stream");
            e.printStackTrace();
        }
        return null;
    }

    protected byte[] readNextNBytes(int n) {
        try {
            return socket.getInputStream().readNBytes(n);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void stop() {
        this.shouldRun = false;
    }

    protected InetAddress getAddress() {
        return socket.getInetAddress();
    }

    public boolean isConnected() {
        return socket.isConnected();
    }
}
