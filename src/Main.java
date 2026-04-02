import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Main class with main method invoked on app start.
 * @version 1.0.0
 * @author Dr. Jody Paul
 */
public class Main {
    /** Private constructor to prevent instantiation of entry point class. */
    private Main() { }

    /**
     * Invoked on start.
     * @param args ignored
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Assignment Tracker");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 450);
        frame.setLocationRelativeTo(null);

        // Table
        String[] columns = {"Assignment", "Due Date", "Done"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public Class<?> getColumnClass(int col) {
                return col == 2 ? Boolean.class : String.class;
            }
        };
        JTable table = new JTable(model);
        table.setRowHeight(24);

        // Input panel
        JTextField nameField = new JTextField(20);
        JTextField dateField = new JTextField(10);
        dateField.setToolTipText("e.g. 2026-04-10");
        JButton addButton = new JButton("Add");
        JButton removeButton = new JButton("Remove Selected");

        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        inputPanel.add(new JLabel("Assignment:"));
        inputPanel.add(nameField);
        inputPanel.add(new JLabel("Due Date:"));
        inputPanel.add(dateField);
        inputPanel.add(addButton);
        inputPanel.add(removeButton);

        // Add action
        addButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            String date = dateField.getText().trim();
            if (!name.isEmpty()) {
                model.addRow(new Object[]{name, date, false});
                nameField.setText("");
                dateField.setText("");
                nameField.requestFocus();
            }
        });

        // Remove action
        removeButton.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                model.removeRow(row);
            }
        });

        frame.setLayout(new BorderLayout());
        frame.add(new JScrollPane(table), BorderLayout.CENTER);
        frame.add(inputPanel, BorderLayout.SOUTH);
        frame.setVisible(true);
    }
}
