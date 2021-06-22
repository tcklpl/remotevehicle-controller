package me.negroni.remotevehicle.controller.api;

public class RemoteVehicle {

    private final VehicleCommunication communication = new VehicleCommunication();

    public RemoteVehicle() {
        communication.startThreads();
    }

    public VehicleCommunication getCommunication() {
        return communication;
    }
}
