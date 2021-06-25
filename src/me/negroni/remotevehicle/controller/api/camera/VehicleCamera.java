package me.negroni.remotevehicle.controller.api.camera;

import me.negroni.remotevehicle.controller.api.VehicleCommunication;
import me.negroni.remotevehicle.controller.api.packet.PacketType;

import java.util.concurrent.Callable;

public class VehicleCamera {

    private CameraImageSize currentResolution;
    private final VehicleCommunication communication;

    public VehicleCamera(VehicleCommunication communication) {
        this.communication = communication;
        this.currentResolution = CameraImageSize.SIZE_HD;
    }

    public void requestResolutionChange(CameraImageSize desiredResolution, Callable<Void> callback) {
        if (desiredResolution == currentResolution) return;
        communication.sendPacket(PacketType.PACKET_CAMERA_CHANGE_RESOLUTION, String.format("%2d", desiredResolution.getCode()));
    }


}
