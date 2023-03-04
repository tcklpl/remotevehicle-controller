# REMOTE VEHICLE: Controller

This is a project in 2 parts, this is the controller part and there's a vehicle part, 
which you can find [here](https://github.com/tcklpl/remotevehicle-vehicle). This part was developed 
using Java.

The code can be found at `src/me.negroni.remotevehicle.controller.api` and a simple usage example can 
be found at `src/me.negroni.remotevehicle.controller.example`.

## Setting up for usage

First you'll need a `RemoteVehicle` object. After that you'll be able to connect, disconnect, send and receive packages
through `RemoteVehicle#getCommunication()` and access the camera to request images or video streams
through `RemoteVehicle#getCamera()`. Refer to the provided example on how to use the package.