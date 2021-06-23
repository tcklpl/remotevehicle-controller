package me.negroni.remotevehicle.controller.api.utils;

import java.nio.charset.StandardCharsets;

public class PacketHeaderUtils {

    public static String extractHeaderFromPacket(byte[] packet) {
        byte[] first4bytes = new byte[4];
        System.arraycopy(packet, 0, first4bytes, 0, 4);
        return new String(first4bytes, StandardCharsets.ISO_8859_1);
    }
}
