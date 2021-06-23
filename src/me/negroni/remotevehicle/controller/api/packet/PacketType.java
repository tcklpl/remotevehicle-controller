package me.negroni.remotevehicle.controller.api.packet;

import java.util.Arrays;

public enum PacketType {
    UNKNOWN_PACKET("UNKNOWN", PackerSender.SERVER),

    PACKET_SERVER_BROADCASTING("CFBC", PackerSender.SERVER),
    PACKET_REQUEST_CONNECTION("CFRC", PackerSender.CLIENT),
    PACKET_ACCEPTED_CONNECTION("CFCK", PackerSender.SERVER),
    PACKET_HEARTBEAT("CFHB", PackerSender.ANY),

    PACKET_REQUEST_CAMERA_IMAGE("DTRC", PackerSender.CLIENT),
    PACKET_CAMERA_IMAGE("DTDC", PackerSender.SERVER),
    PACKET_IMAGE_DONE("CUSTOM-IMAGE-DONE", PackerSender.NONE);

    private final String code;
    private final PackerSender sender;

    PacketType(String code, PackerSender sender) {
        this.code = code;
        this.sender = sender;
    }

    public String getCode() {
        return code;
    }

    public PackerSender getSender() {
        return sender;
    }

    public enum PackerSender {
        SERVER, CLIENT, ANY, NONE
    }

    public static PacketType getPacketTypeByCode(String code) {
        return Arrays.stream(values()).filter(p -> p.getCode().equals(code)).findFirst().orElse(UNKNOWN_PACKET);
    }
}
