package me.negroni.remotevehicle.controller.api.camera;

import me.negroni.remotevehicle.controller.api.VehicleCommunication;
import me.negroni.remotevehicle.controller.api.packet.ImageContainer;
import me.negroni.remotevehicle.controller.api.packet.PacketType;
import me.negroni.remotevehicle.controller.api.packet.callback.MutuallyExclusiveCallback;

import java.util.function.Consumer;

public class VehicleCamera {

    private CameraImageSize currentResolution;
    private final VehicleCommunication communication;

    public VehicleCamera(VehicleCommunication communication) {
        this.communication = communication;
        this.currentResolution = CameraImageSize.SIZE_HD;
    }

    public void requestResolutionChange(CameraImageSize desiredResolution, Runnable success, Runnable error) {
        if (desiredResolution == currentResolution) return;

        MutuallyExclusiveCallback resChangedCallback = new MutuallyExclusiveCallback(1);
        resChangedCallback.addCallback(PacketType.PACKET_CAMERA_RESOLUTION_CHANGED, c -> {
            currentResolution = desiredResolution;
            success.run();
        });
        resChangedCallback.addCallback(PacketType.PACKET_ERROR_CHANGING_CAMERA_RESOLUTION, c -> error.run());
        communication.registerMutuallyExclusiveCallback(resChangedCallback);

        communication.sendPacket(PacketType.PACKET_CAMERA_CHANGE_RESOLUTION, String.format("%02d", desiredResolution.getCode()));
    }

    public void requestCameraImage(Consumer<byte[]> callback) {
        communication.registerCustomLimitedCallback(PacketType.PACKET_CAMERA_IMAGE, 1, c -> callback.accept(((ImageContainer) c).getImage()));
        communication.sendPacket(PacketType.PACKET_REQUEST_CAMERA_IMAGE);
    }

    public CameraImageSize getCurrentResolution() {
        return currentResolution;
    }
}
