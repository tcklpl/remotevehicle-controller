package me.negroni.remotevehicle.controller.tests;

import me.negroni.remotevehicle.controller.api.RemoteVehicle;
import me.negroni.remotevehicle.controller.api.packet.ImageContainer;
import me.negroni.remotevehicle.controller.api.packet.PacketType;

import javax.swing.*;

public class GuiTest {

    private JLabel titleLabel;
    private JPanel mainPanel;
    private JButton btnRequestImg;
    private JPanel panelImg;
    private JLabel imgLabel;
    private JButton btnDisconnect;
    private JButton btnConnect;

    private final RemoteVehicle remoteVehicle;

    public GuiTest() {
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

        btnRequestImg.addActionListener(e -> {
            btnRequestImg.setEnabled(false);
            remoteVehicle.getCommunication().sendPacket(PacketType.PACKET_REQUEST_CAMERA_IMAGE);
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

        remoteVehicle.getCommunication().registerCustomCallback(PacketType.PACKET_CAMERA_IMAGE, c -> {
            ImageContainer ic = (ImageContainer) c;
            imgLabel.setIcon(new ImageIcon(ic.getImage()));
            btnRequestImg.setEnabled(true);
        });

        remoteVehicle.getCommunication().registerCustomCallback(PacketType.PACKET_CONFIRM_CONNECTION_END, c -> {
            titleLabel.setText("Status: NÃO CONECTADO");
            btnConnect.setEnabled(true);
            btnRequestImg.setEnabled(false);
        });
    }
}
