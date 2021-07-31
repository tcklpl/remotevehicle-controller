package me.negroni.remotevehicle.controller.api.sockets;

import me.negroni.remotevehicle.controller.api.packet.ImageContainer;
import me.negroni.remotevehicle.controller.api.packet.PacketContainer;
import me.negroni.remotevehicle.controller.api.packet.PacketProcessor;
import me.negroni.remotevehicle.controller.api.packet.PacketType;
import me.negroni.remotevehicle.controller.api.utils.PacketHeaderUtils;

import java.net.InetAddress;

public class ImageTcpSocket extends GenericTcpSocket implements Runnable {

    private final PacketProcessor packetProcessor;

    public ImageTcpSocket(PacketProcessor packetProcessor, InetAddress address, int port) {
        super(address, port);
        this.packetProcessor = packetProcessor;
    }

    @Override
    public void run() {
        while (shouldRun) {

            writeIfTheresAnything();

            byte[] read = read(10);
            if (read != null) {
                PacketType type = PacketType.getPacketTypeByCode(PacketHeaderUtils.extractHeaderFromPacket(read));
                PacketContainer container;

                if (type == PacketType.PACKET_CAMERA_IMAGE) {
                    ImageContainer ic = new ImageContainer(read, getAddress());
                    byte[] image = readNextNBytes(ic.getImageLength());
                    ic.setImage(image);
                    container = ic;
                } else {
                    container = new PacketContainer(read, getAddress());
                }

                packetProcessor.acceptPacket(container);
            }
        }
    }

}
