

package com.mycompany.telephone_system;


import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class TelephoneSystemSimulator extends JFrame {
    private JLabel lblFrom, lblTo, lblLength, lblArrival, lblClock;
    private JLabel[] lineLabels;
    private JLabel lblMaxLinks, lblLinksInUse;
    private JTable callsTable;
    private JLabel lblProcessed, lblCompleted, lblBlocked, lblBusy;

    private int clock = 1050;
    private int callCounter = 0;
    private final Random random = new Random();

    // Track line status: true = busy, false = free
    private final boolean[] linesBusy = new boolean[7];

    // List of active calls
    private final List<Call> activeCalls = new ArrayList<>();

    // Counters
    private int processed = 132, completed = 99, blocked = 5, busy = 0;
    private final int maxLinks = 3; // max concurrent calls (links)

    // Model for calls table
    private DefaultTableModel tableModel;

    public TelephoneSystemSimulator() {
        setTitle("Telephone Simulation System");
        setSize(850, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new GridLayout(2, 1));
        topPanel.setBorder(new TitledBorder("Next Call"));

        JPanel callDetailsPanel = new JPanel(new GridLayout(1, 6));
        callDetailsPanel.add(new JLabel("From:"));
        lblFrom = new JLabel("-");
        callDetailsPanel.add(lblFrom);
        callDetailsPanel.add(new JLabel("To:"));
        lblTo = new JLabel("-");
        callDetailsPanel.add(lblTo);
        callDetailsPanel.add(new JLabel("Length:"));
        lblLength = new JLabel("-");
        callDetailsPanel.add(lblLength);

        JPanel arrivalPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        arrivalPanel.add(new JLabel("Arrival Time:"));
        lblArrival = new JLabel("-");
        arrivalPanel.add(lblArrival);

        topPanel.add(callDetailsPanel);
        topPanel.add(arrivalPanel);
        add(topPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridLayout(1, 3));

        // Lines panel
        JPanel linesPanel = new JPanel(new GridLayout(7, 2));
        linesPanel.setBorder(new TitledBorder("Lines"));
        lineLabels = new JLabel[7];
        for (int i = 0; i < 7; i++) {
            linesPanel.add(new JLabel("Line " + i));
            lineLabels[i] = new JLabel("Free");
            lineLabels[i].setForeground(Color.GREEN.darker());
            linesPanel.add(lineLabels[i]);
        }
        centerPanel.add(linesPanel);

        // Mid panel: Links + Clock
        JPanel midPanel = new JPanel(new GridLayout(3, 1));
        JPanel linksPanel = new JPanel(new GridLayout(2, 2));
        linksPanel.setBorder(new TitledBorder("Links"));
        linksPanel.add(new JLabel("Max No:"));
        lblMaxLinks = new JLabel(String.valueOf(maxLinks));
        linksPanel.add(lblMaxLinks);
        linksPanel.add(new JLabel("In Use:"));
        lblLinksInUse = new JLabel("0");
        linksPanel.add(lblLinksInUse);

        JPanel clockPanel = new JPanel();
        clockPanel.setBorder(new TitledBorder("Clock"));
        lblClock = new JLabel(String.valueOf(clock));
        clockPanel.add(lblClock);

        midPanel.add(linksPanel);
        midPanel.add(clockPanel);
        centerPanel.add(midPanel);

        // Calls in progress table
        String[] colNames = {"From", "To", "End Time"};
        tableModel = new DefaultTableModel(colNames, 0);
        callsTable = new JTable(tableModel);
        JScrollPane callScrollPane = new JScrollPane(callsTable);
        callScrollPane.setBorder(new TitledBorder("Calls in Progress"));
        centerPanel.add(callScrollPane);

        add(centerPanel, BorderLayout.CENTER);

        // Bottom panel with counters and buttons
        JPanel bottomPanel = new JPanel(new GridLayout(2, 4));
        bottomPanel.setBorder(new TitledBorder("Call Counters"));

        lblProcessed = new JLabel("Processed: " + processed);
        lblCompleted = new JLabel("Completed: " + completed);
        lblBlocked = new JLabel("Blocked: " + blocked);
        lblBusy = new JLabel("Busy: " + busy);

        bottomPanel.add(lblProcessed);
        bottomPanel.add(lblCompleted);
        bottomPanel.add(lblBlocked);
        bottomPanel.add(lblBusy);

        JButton btnStart = new JButton("Start");
        btnStart.addActionListener(e -> startSimulation());

        JButton btnStop = new JButton("Stop");
        btnStop.addActionListener(e -> {
            new home().setVisible(true); // Open home screen
            this.dispose();              // Close current simulator window
        });

        bottomPanel.add(btnStart);
        bottomPanel.add(btnStop);
        add(bottomPanel, BorderLayout.SOUTH);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void startSimulation() {
        Timer timer = new Timer();

        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                clock++;
                SwingUtilities.invokeLater(() -> {
                    lblClock.setText(String.valueOf(clock));
                    processed++;
                    lblProcessed.setText("Processed: " + processed);

                    // First, check for ended calls and remove them
                    Iterator<Call> iterator = activeCalls.iterator();
                    boolean freedLine = false;
                    while (iterator.hasNext()) {
                        Call c = iterator.next();
                        if (c.endTime <= clock) {
                            // Call ended, free line
                            linesBusy[c.from] = false;
                            lineLabels[c.from].setText("Free");
                            lineLabels[c.from].setForeground(Color.GREEN.darker());
                            iterator.remove();

                            // Remove call from table
                            for (int i = 0; i < tableModel.getRowCount(); i++) {
                                int fromVal = Integer.parseInt(tableModel.getValueAt(i, 0).toString());
                                int endVal = Integer.parseInt(tableModel.getValueAt(i, 2).toString());
                                if (fromVal == c.from && endVal == c.endTime) {
                                    tableModel.removeRow(i);
                                    break;
                                }
                            }
                            completed++;
                            lblCompleted.setText("Completed: " + completed);
                            busy--;
                            lblBusy.setText("Busy: " + busy);
                            freedLine = true;
                        }
                    }

                    // Update links in use
                    lblLinksInUse.setText(String.valueOf(busy));

                    // Generate new call if links in use less than maxLinks
                    if (busy < maxLinks) {
                        int from = random.nextInt(7);
                        int to = random.nextInt(7);
                        while (to == from) {
                            to = random.nextInt(7);
                        }
                        int length = 10 + random.nextInt(20);
                        int end = clock + length;

                        lblFrom.setText(String.valueOf(from));
                        lblTo.setText(String.valueOf(to));
                        lblLength.setText(String.valueOf(length));
                        lblArrival.setText(String.valueOf(clock));

                        if (!linesBusy[from]) {
                            // Line free, accept call
                            linesBusy[from] = true;
                            lineLabels[from].setText("Busy");
                            lineLabels[from].setForeground(Color.RED.darker());
                            busy++;
                            lblBusy.setText("Busy: " + busy);

                            // Add call to activeCalls list
                            activeCalls.add(new Call(from, to, end));

                            // Add call to table
                            tableModel.addRow(new Object[]{from, to, end});
                        } else {
                            // Line busy, block call
                            blocked++;
                            lblBlocked.setText("Blocked: " + blocked);

                            // Show blocked call details temporarily
                            lblFrom.setText(String.valueOf(from));
                            lblTo.setText(String.valueOf(to));
                            lblLength.setText(String.valueOf(length));
                            lblArrival.setText(String.valueOf(clock));
                        }
                    } else {
                        // No links available, block call (simulate arrival)
                        blocked++;
                        lblBlocked.setText("Blocked: " + blocked);

                        // Show blocked call details temporarily with random numbers
                        int from = random.nextInt(7);
                        int to = random.nextInt(7);
                        while (to == from) to = random.nextInt(7);
                        int length = 10 + random.nextInt(20);

                        lblFrom.setText(String.valueOf(from));
                        lblTo.setText(String.valueOf(to));
                        lblLength.setText(String.valueOf(length));
                        lblArrival.setText(String.valueOf(clock));
                    }

                });
            }
        }, 0, 500);
    }

    private static class Call {
        int from;
        int to;
        int endTime;

        public Call(int from, int to, int endTime) {
            this.from = from;
            this.to = to;
            this.endTime = endTime;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TelephoneSystemSimulator());
    }
}