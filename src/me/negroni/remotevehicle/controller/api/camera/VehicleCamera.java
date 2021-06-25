package me.negroni.remotevehicle.controller.api.camera;

import me.negroni.remotevehicle.controller.api.VehicleCommunication;
import me.negroni.remotevehicle.controller.api.packet.ImageContainer;
import me.negroni.remotevehicle.controller.api.packet.PacketType;

import java.util.function.Consumer;

public class VehicleCamera {

    private CameraImageSize currentResolution;
    private final VehicleCommunication communication;

    public VehicleCamera(VehicleCommunication communication) {
        this.communication = communication;
        this.currentResolution = CameraImageSize.SIZE_HD;
    }

    public void requestResolutionChange(CameraImageSize desiredResolution, Runnable callback) {
        if (desiredResolution == currentResolution) return;
        communication.registerCustomLimitedCallback(PacketType.PACKET_CAMERA_RESOLUTION_CHANGED, 1, c -> {
            currentResolution = desiredResolution;
            callback.run();
        });
        communication.sendPacket(PacketType.PACKET_CAMERA_CHANGE_RESOLUTION, String.format("%2d", desiredResolution.getCode()));
    }

    public void requestCameraImage(Consumer<byte[]> callback) {
        communication.registerCustomLimitedCallback(PacketType.PACKET_CAMERA_IMAGE, 1, c -> callback.accept(((ImageContainer) c).getImage()));
        communication.sendPacket(PacketType.PACKET_REQUEST_CAMERA_IMAGE);
    }

    public CameraImageSize getCurrentResolution() {
        return currentResolution;
    }
}
