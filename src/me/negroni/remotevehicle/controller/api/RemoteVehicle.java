package me.negroni.remotevehicle.controller.api;

import me.negroni.remotevehicle.controller.api.camera.VehicleCamera;

public class RemoteVehicle {

    private final VehicleCommunication communication = new VehicleCommunication();
    private final VehicleCamera camera = new VehicleCamera(communication);

    public RemoteVehicle() {
        communication.startThreads();
    }

    public VehicleCommunication getCommunication() {
        return communication;
    }

    public VehicleCamera getCamera() {
        return camera;
    }
}
