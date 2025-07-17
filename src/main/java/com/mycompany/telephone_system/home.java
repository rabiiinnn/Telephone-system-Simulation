

package com.mycompany.telephone_system;



import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class home extends JFrame {

    public home() {
        setTitle("Telephone System Simulation - Control Panel");
        setSize(900, 520);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);

        // --- Buttons ---
        JButton btnLostCall = new JButton("Lost Call Simulation");
        btnLostCall.setBounds(50, 400, 180, 40);
        btnLostCall.setFont(new Font("Tahoma", Font.BOLD, 14));

        JButton btnDelayCall = new JButton("Delay Call Simulation");
        btnDelayCall.setBounds(660, 400, 180, 40);
        btnDelayCall.setFont(new Font("Tahoma", Font.BOLD, 14));

        btnLostCall.addActionListener((ActionEvent e) -> {
            SwingUtilities.invokeLater(() -> new TelephoneSystemSimulator());
        });

        btnDelayCall.addActionListener((ActionEvent e) -> {
            SwingUtilities.invokeLater(() -> new DelayCallSimulator());
        });

        add(btnLostCall);
        add(btnDelayCall);

        // --- Header Title ---
        JLabel titleLabel = new JLabel("Welcome to Telephone Simulation System", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 28));
        titleLabel.setBounds(80, 20, 720, 40);
        add(titleLabel);

        // --- College Logo ---
        ImageIcon icon = new ImageIcon("C:\\Users\\ACER\\Downloads\\college_logo.png");
 // <-- Replace with actual path
        Image scaledImage = icon.getImage().getScaledInstance(160, 160, Image.SCALE_SMOOTH);
        JLabel logoLabel = new JLabel(new ImageIcon(scaledImage));
        logoLabel.setBounds(370, 80, 160, 160);
        add(logoLabel);

        // --- Info Footer ---
        JLabel devLabel = new JLabel("Rabin Adhikari", SwingConstants.CENTER);
        devLabel.setFont(new Font("Tahoma", Font.PLAIN, 18));
        devLabel.setBounds(250, 270, 400, 25);
        add(devLabel);

        JLabel rollLabel = new JLabel("022339", SwingConstants.CENTER);
        rollLabel.setFont(new Font("Tahoma", Font.PLAIN, 16));
        rollLabel.setBounds(250, 300, 400, 20);
        add(rollLabel);

        JLabel dateLabel = new JLabel("2082-04-01", SwingConstants.CENTER);
        dateLabel.setFont(new Font("Tahoma", Font.PLAIN, 16));
        dateLabel.setBounds(250, 330, 400, 20);
        add(dateLabel);

        JLabel deptLabel = new JLabel("Department of Computer Science and Engineering", SwingConstants.CENTER);
        deptLabel.setFont(new Font("Tahoma", Font.PLAIN, 16));
        deptLabel.setBounds(180, 360, 550, 20);
        add(deptLabel);

        JLabel collegeLabel = new JLabel("Nepal Engineering College", SwingConstants.CENTER);
        collegeLabel.setFont(new Font("Tahoma", Font.BOLD, 18));
        collegeLabel.setBounds(250, 390, 400, 25);
         add(collegeLabel);

        setLocationRelativeTo(null); // Center the window
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new home());
    }
}
