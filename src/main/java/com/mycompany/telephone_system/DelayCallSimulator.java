

package com.mycompany.telephone_system;




import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class DelayCallSimulator extends JFrame {
    private JLabel lblFrom, lblTo, lblLength, lblArrival, lblClock;
    private JLabel[] lineLabels;
    private JLabel lblMaxLinks, lblLinksInUse;
    private JTable callsTable;
    private JLabel lblDelayFrom, lblDelayTo, lblDelayLength;
    private JLabel lblProcessed, lblCompleted, lblBlocked, lblBusy;

    private final boolean[] linesBusy = new boolean[7];
    private final List<Call> activeCalls = new ArrayList<>();
    private final Queue<Call> delayQueue = new LinkedList<>();
    private DefaultTableModel tableModel;

    private int clock = 1050;
    private int processed = 132, completed = 99, blocked = 3, busy = 0;
    private final int maxLinks = 3;
    private final Random random = new Random();

    public DelayCallSimulator() {
        setTitle("Delay Call Simulation Layout");
        setSize(850, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Top panel for next call details
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

        // Center panel holds lines, links & clock, calls in progress, and delayed call details
        JPanel centerPanel = new JPanel(new GridLayout(1, 4));

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

        // Mid panel for links and clock
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

        // Delayed call details panel
        JPanel delayPanel = new JPanel(new GridLayout(2, 3));
        delayPanel.setBorder(new TitledBorder("Delayed Calls"));
        delayPanel.add(new JLabel("From"));
        delayPanel.add(new JLabel("To"));
        delayPanel.add(new JLabel("Length"));
        lblDelayFrom = new JLabel("-");
        lblDelayTo = new JLabel("-");
        lblDelayLength = new JLabel("-");
        delayPanel.add(lblDelayFrom);
        delayPanel.add(lblDelayTo);
        delayPanel.add(lblDelayLength);

        midPanel.add(delayPanel);

        centerPanel.add(midPanel);

        // Calls in progress table
        String[] colNames = {"From", "To", "End Time"};
        tableModel = new DefaultTableModel(colNames, 0);
        callsTable = new JTable(tableModel);
        JScrollPane callScrollPane = new JScrollPane(callsTable);
        callScrollPane.setBorder(new TitledBorder("Calls in Progress"));
        centerPanel.add(callScrollPane);

        add(centerPanel, BorderLayout.CENTER);

        // Bottom panel for counters and control buttons
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

                    // Complete calls whose end time is reached
                    Iterator<Call> iterator = activeCalls.iterator();
                    while (iterator.hasNext()) {
                        Call call = iterator.next();
                        if (call.endTime <= clock) {
                            linesBusy[call.from] = false;
                            lineLabels[call.from].setText("Free");
                            lineLabels[call.from].setForeground(Color.GREEN.darker());
                            iterator.remove();

                            // Remove from table
                            for (int i = 0; i < tableModel.getRowCount(); i++) {
                                int fromVal = Integer.parseInt(tableModel.getValueAt(i, 0).toString());
                                int endVal = Integer.parseInt(tableModel.getValueAt(i, 2).toString());
                                if (fromVal == call.from && endVal == call.endTime) {
                                    tableModel.removeRow(i);
                                    break;
                                }
                            }
                            completed++;
                            lblCompleted.setText("Completed: " + completed);
                            busy--;
                        }
                    }

                    lblLinksInUse.setText(String.valueOf(busy));
                    lblBusy.setText("Busy: " + busy);

                    // Retry delayed calls first if links available
                    if (!delayQueue.isEmpty() && busy < maxLinks) {
                        Call delayed = delayQueue.poll();
                        lblDelayFrom.setText(String.valueOf(delayed.from));
                        lblDelayTo.setText(String.valueOf(delayed.to));
                        lblDelayLength.setText(String.valueOf(delayed.endTime - clock));

                        if (!linesBusy[delayed.from]) {
                            activateCall(delayed);
                        } else {
                            // Line still busy, put back
                            delayQueue.add(delayed);
                        }
                    } else {
                        // Clear delayed call display if no delayed call
                        lblDelayFrom.setText("-");
                        lblDelayTo.setText("-");
                        lblDelayLength.setText("-");
                    }

                    // Generate a new incoming call
                    int from = random.nextInt(7);
                    int to = random.nextInt(7);
                    while (to == from) to = random.nextInt(7);
                    int length = 10 + random.nextInt(20);
                    int end = clock + length;

                    lblFrom.setText(String.valueOf(from));
                    lblTo.setText(String.valueOf(to));
                    lblLength.setText(String.valueOf(length));
                    lblArrival.setText(String.valueOf(clock));

                    Call newCall = new Call(from, to, end);

                    if (busy < maxLinks && !linesBusy[from]) {
                        activateCall(newCall);
                    } else {
                        // Delay the call instead of blocking
                        delayQueue.add(newCall);
                        blocked++;
                        lblBlocked.setText("Blocked: " + blocked);
                    }
                });
            }
        }, 0, 600);
    }

    private void activateCall(Call call) {
        linesBusy[call.from] = true;
        lineLabels[call.from].setText("Busy");
        lineLabels[call.from].setForeground(Color.RED.darker());
        activeCalls.add(call);
        tableModel.addRow(new Object[]{call.from, call.to, call.endTime});
        busy++;
        lblBusy.setText("Busy: " + busy);
        lblLinksInUse.setText(String.valueOf(busy));
    }

    private static class Call {
        int from, to, endTime;
        public Call(int from, int to, int endTime) {
            this.from = from;
            this.to = to;
            this.endTime = endTime;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DelayCallSimulator());
    }
}