package me.negroni.remotevehicle.controller.api.sockets;

import me.negroni.remotevehicle.controller.api.packet.ImageContainer;
import me.negroni.remotevehicle.controller.api.packet.PacketProcessor;
import me.negroni.remotevehicle.controller.api.packet.PacketType;
import me.negroni.remotevehicle.controller.api.utils.PacketHeaderUtils;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class ImageTcpSocket implements Runnable {

    private Socket socket;
    private boolean shouldRun;

    private final PacketProcessor packetProcessor;


    public ImageTcpSocket(PacketProcessor packetProcessor, InetAddress address, int port) {
        this.packetProcessor = packetProcessor;
        try {
            socket = new Socket(address, port);
            socket.setTcpNoDelay(true);
            shouldRun = true;
            System.out.println("Successfully created image tcp socket to " + address.getHostAddress() + ":" + port);
        } catch (IOException e) {
            System.err.println("Failed to create image tcp socket to " + address.getHostAddress() + ":" + port);
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (shouldRun) {
            // read if there is anything to read
            try {
                byte[] inputData = new byte[10];
                int result = socket.getInputStream().read(inputData, 0, socket.getInputStream().available());
                if (result > 0) {

                    if (PacketType.getPacketTypeByCode(PacketHeaderUtils.extractHeaderFromPacket(inputData)) == PacketType.PACKET_CAMERA_IMAGE) {

                        ImageContainer ic = new ImageContainer(inputData, socket.getInetAddress());
                        byte[] image = socket.getInputStream().readNBytes(ic.getImageLength());
                        ic.setImage(image);
                        packetProcessor.acceptPacket(ic);

                    } else {
                        System.out.println("unrecognized packet at image tcp socket");
                    }

                }
            } catch (IOException e) {
                System.err.println("Failed to read img tcp socket in stream");
                e.printStackTrace();
            }
        }
    }

    public void stop() {
        this.shouldRun = false;
    }

}
