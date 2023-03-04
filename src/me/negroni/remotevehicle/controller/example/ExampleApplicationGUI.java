package me.negroni.remotevehicle.controller.example;

import me.negroni.remotevehicle.controller.api.RemoteVehicle;
import me.negroni.remotevehicle.controller.api.camera.CameraImageSize;
import me.negroni.remotevehicle.controller.api.packet.PacketType;

import javax.swing.*;
import java.util.Arrays;

public class ExampleApplicationGUI {

    private JLabel titleLabel;
    private JPanel mainPanel;
    private JButton btnRequestImg;
    private JPanel panelImg;
    private JLabel imgLabel;
    private JButton btnDisconnect;
    private JButton btnConnect;
    private JComboBox<CameraImageSize> cmbRes;
    private JButton btnRes;
    private JLabel lblRes;

    private final RemoteVehicle remoteVehicle;

    public ExampleApplicationGUI() {
        // First of all we need an instance of RemoveVehicle
        remoteVehicle = new RemoteVehicle();
        // Function below, register callbacks for connecting and disconnecting
        register();

        // GUI Window Initialization
        JFrame frame = new JFrame("test");
        frame.setContentPane(mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1280, 720);
        frame.pack();
        btnConnect.setEnabled(true);
        btnDisconnect.setEnabled(false);
        btnRequestImg.setEnabled(false);
        frame.setVisible(true);

        // Add all available resolutions to a GUI combo box
        Arrays.stream(CameraImageSize.values()).forEach(x -> cmbRes.addItem(x));

        // When the user presses the 'Request Image' button we'll invoke VehicleCamera#requestCameraImage, this
        // function will require a consumer for an array of bytes (byte[]).
        btnRequestImg.addActionListener(e -> remoteVehicle.getCamera().requestCameraImage(b -> {
            imgLabel.setIcon(new ImageIcon(b));
            btnRequestImg.setEnabled(true);
        }));

        // When the user presses the 'Change Resolution' button we'll try to change the camera resolution through
        // VehicleCamera#requestResolutionChange. This function required the desired resolution and two callbacks:
        // the first one for success and the second one if the operation fails.
        btnRes.addActionListener(e -> {
            lblRes.setText("Resolução: (Mudando)");
            remoteVehicle.getCamera().requestResolutionChange((CameraImageSize) cmbRes.getSelectedItem(),
                    () -> lblRes.setText("Resolução:"),
                    () -> lblRes.setText("Resolução: (ERRO)"));
        });

        // As the vehicle is constantly sending a connection package on the network, we only need to allow the
        // controller to respond to it and start the connection sequence.
        btnConnect.addActionListener(e -> {
            remoteVehicle.getCommunication().setShouldTryToConnect(true);
            btnConnect.setEnabled(false);
        });

        // Disconnect from the vehicle
        btnDisconnect.addActionListener(e -> {
            remoteVehicle.getCommunication().disconnect();
            btnDisconnect.setEnabled(false);
        });
    }

    private void register() {
        // Update title and button status when the controller connects to the vehicle
        remoteVehicle.getCommunication().registerCustomCallback(PacketType.PACKET_ACCEPTED_CONNECTION, c -> {
            titleLabel.setText("Status: CONECTADO (" + c.getRemoteAddress().toString() + ")");
            btnDisconnect.setEnabled(true);
            btnRequestImg.setEnabled(true);
        });

        // Update title and button status when the controller disconnects from the vehicle
        remoteVehicle.getCommunication().registerCustomCallback(PacketType.PACKET_CONFIRM_CONNECTION_END, c -> {
            titleLabel.setText("Status: NÃO CONECTADO");
            btnConnect.setEnabled(true);
            btnRequestImg.setEnabled(false);
        });
    }
}
