package me.negroni.remotevehicle.controller.api.packet;

import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

public class ImageContainer extends PacketContainer {

    private int imageLength;
    private byte[] image;

    public ImageContainer(byte[] rawData, InetAddress remoteAddress) {
        super(rawData, remoteAddress);
        String headerImageLength = new String(getWholePacket(), StandardCharsets.ISO_8859_1).substring(4, 10).trim();
        try {
            this.imageLength = Integer.parseInt(headerImageLength);
            System.out.println("receiving image with length: " + imageLength);
        } catch (Exception e) {
            System.out.println("failed to convert integer");
            e.printStackTrace();
        }
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public int getImageLength() {
        return imageLength;
    }

    public byte[] getImage() {
        return image;
    }
}
