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
        remoteVehicle = new RemoteVehicle();
        register();
        JFrame frame = new JFrame("test");
        frame.setContentPane(mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1280, 720);
        frame.pack();
        btnConnect.setEnabled(true);
        btnDisconnect.setEnabled(false);
        btnRequestImg.setEnabled(false);
        frame.setVisible(true);

        Arrays.stream(CameraImageSize.values()).forEach(x -> cmbRes.addItem(x));

        btnRequestImg.addActionListener(e -> remoteVehicle.getCamera().requestCameraImage(b -> {
            imgLabel.setIcon(new ImageIcon(b));
            btnRequestImg.setEnabled(true);
        }));

        btnRes.addActionListener(e -> {
            lblRes.setText("Resolução: (Mudando)");
            remoteVehicle.getCamera().requestResolutionChange((CameraImageSize) cmbRes.getSelectedItem(),
                    () -> lblRes.setText("Resolução:"),
                    () -> lblRes.setText("Resolução: (ERRO)"));
        });

        btnConnect.addActionListener(e -> {
            remoteVehicle.getCommunication().setShouldTryToConnect(true);
            btnConnect.setEnabled(false);
        });

        btnDisconnect.addActionListener(e -> {
            remoteVehicle.getCommunication().disconnect();
            btnDisconnect.setEnabled(false);
        });
    }

    private void register() {
        remoteVehicle.getCommunication().registerCustomCallback(PacketType.PACKET_ACCEPTED_CONNECTION, c -> {
            titleLabel.setText("Status: CONECTADO (" + c.getRemoteAddress().toString() + ")");
            btnDisconnect.setEnabled(true);
            btnRequestImg.setEnabled(true);
        });

        remoteVehicle.getCommunication().registerCustomCallback(PacketType.PACKET_CONFIRM_CONNECTION_END, c -> {
            titleLabel.setText("Status: NÃO CONECTADO");
            btnConnect.setEnabled(true);
            btnRequestImg.setEnabled(false);
        });
    }
}
