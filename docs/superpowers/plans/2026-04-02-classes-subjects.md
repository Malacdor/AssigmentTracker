# Classes/Subjects Feature Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add create/rename/delete class management with a sidebar for navigation and per-class assignment lists.

**Architecture:** A new `Subject.java` data class owns each class's name and its `DefaultTableModel`. `Main.java`'s `createAndShowGUI` is rewritten to add a dark sidebar (`JPanel`, WEST) containing a `JList<Subject>` and three management buttons. A `ListSelectionListener` swaps the center table's model on selection change. All event handlers derive current state from `subjectList.getSelectedValue()` — no reassignable captured locals.

**Tech Stack:** Java Swing, Apache Ant, JDK 25 (CI), Java 1.8 syntax

---

### Task 1: Create Subject.java

**Files:**
- Create: `src/Subject.java`

- [ ] **Step 1: Create `src/Subject.java`**

```java
import javax.swing.table.DefaultTableModel;

/**
 * Represents a class or subject that holds a list of assignments.
 */
public class Subject {
    private String name;
    private final DefaultTableModel tableModel;

    public Subject(String name) {
        this.name = name;
        this.tableModel = new DefaultTableModel(
                new String[]{"Assignment", "Due Date", "Done"}, 0) {
            @Override
            public Class<?> getColumnClass(int col) {
                return col == 2 ? Boolean.class : String.class;
            }
        };
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public DefaultTableModel getTableModel() { return tableModel; }

    @Override
    public String toString() { return name; }
}
```

- [ ] **Step 2: Verify compilation**

```bash
ant compile
```

Expected: `BUILD SUCCESSFUL` with no errors.

- [ ] **Step 3: Commit**

```bash
git add src/Subject.java
git commit -m "feat: add Subject data class (#15)"
```

---

### Task 2: Rewrite Main.java

**Files:**
- Modify: `src/Main.java`

All UI state is declared up front as effectively-final locals so lambdas can capture them freely. Every action listener reads the current subject from `subjectList.getSelectedValue()` rather than a reassigned field.

- [ ] **Step 1: Replace `src/Main.java` with the following**

```java
import javax.swing.*;
import javax.swing.event.*;
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
        frame.setSize(750, 550);
        frame.setLocationRelativeTo(null);

        // --- Data models ---
        DefaultListModel<Subject> subjectListModel = new DefaultListModel<>();
        DefaultTableModel emptyTableModel = new DefaultTableModel(
                new String[]{"Assignment", "Due Date", "Done"}, 0) {
            @Override
            public Class<?> getColumnClass(int col) {
                return col == 2 ? Boolean.class : String.class;
            }
        };

        // --- Center: assignment table ---
        JTable table = new JTable(emptyTableModel);
        table.setRowHeight(24);

        // --- South: input panel (disabled until a class is selected) ---
        JTextField nameField = new JTextField(20);
        JTextField dateField = new JTextField(10);
        dateField.setToolTipText("e.g. 2026-04-10");
        JButton addButton = new JButton("Add");
        JButton removeButton = new JButton("Remove Selected");
        nameField.setEnabled(false);
        dateField.setEnabled(false);
        addButton.setEnabled(false);
        removeButton.setEnabled(false);

        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        inputPanel.add(new JLabel("Assignment:"));
        inputPanel.add(nameField);
        inputPanel.add(new JLabel("Due Date:"));
        inputPanel.add(dateField);
        inputPanel.add(addButton);
        inputPanel.add(removeButton);

        // --- West: sidebar ---
        JList<Subject> subjectList = new JList<>(subjectListModel);
        subjectList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        subjectList.setBackground(new Color(44, 44, 44));
        subjectList.setForeground(Color.WHITE);
        subjectList.setSelectionBackground(new Color(70, 70, 70));
        subjectList.setSelectionForeground(Color.WHITE);
        subjectList.setFont(subjectList.getFont().deriveFont(13f));

        JButton newClassButton = new JButton("New Class");
        JButton renameButton = new JButton("Rename");
        JButton deleteButton = new JButton("Delete");
        renameButton.setEnabled(false);
        deleteButton.setEnabled(false);

        JPanel sidebarButtonPanel = new JPanel(new GridLayout(3, 1, 0, 2));
        sidebarButtonPanel.add(newClassButton);
        sidebarButtonPanel.add(renameButton);
        sidebarButtonPanel.add(deleteButton);
        sidebarButtonPanel.setBackground(new Color(44, 44, 44));

        JScrollPane sidebarScroll = new JScrollPane(subjectList);
        sidebarScroll.getViewport().setBackground(new Color(44, 44, 44));

        JPanel sidebarPanel = new JPanel(new BorderLayout(0, 4));
        sidebarPanel.setPreferredSize(new Dimension(150, 0));
        sidebarPanel.setBackground(new Color(44, 44, 44));
        sidebarPanel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        sidebarPanel.add(sidebarScroll, BorderLayout.CENTER);
        sidebarPanel.add(sidebarButtonPanel, BorderLayout.SOUTH);

        // --- Selection listener: swap table model, toggle controls ---
        subjectList.addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            Subject selected = subjectList.getSelectedValue();
            boolean has = selected != null;
            table.setModel(has ? selected.getTableModel() : emptyTableModel);
            nameField.setEnabled(has);
            dateField.setEnabled(has);
            addButton.setEnabled(has);
            removeButton.setEnabled(has);
            renameButton.setEnabled(has);
            deleteButton.setEnabled(has);
        });

        // --- Add assignment ---
        addButton.addActionListener(e -> {
            Subject selected = subjectList.getSelectedValue();
            if (selected == null) return;
            String name = nameField.getText().trim();
            String date = dateField.getText().trim();
            if (!name.isEmpty()) {
                selected.getTableModel().addRow(new Object[]{name, date, false});
                nameField.setText("");
                dateField.setText("");
                nameField.requestFocus();
            }
        });

        // --- Remove assignment ---
        removeButton.addActionListener(e -> {
            Subject selected = subjectList.getSelectedValue();
            if (selected == null) return;
            int row = table.getSelectedRow();
            if (row >= 0) {
                selected.getTableModel().removeRow(row);
            }
        });

        // --- New Class ---
        newClassButton.addActionListener(e -> {
            String input = JOptionPane.showInputDialog(frame, "Class name:", "New Class",
                    JOptionPane.PLAIN_MESSAGE);
            if (input == null) return;
            String name = input.trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Class name cannot be empty.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            for (int i = 0; i < subjectListModel.size(); i++) {
                if (subjectListModel.get(i).getName().equalsIgnoreCase(name)) {
                    JOptionPane.showMessageDialog(frame, "A class with that name already exists.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            Subject subject = new Subject(name);
            subjectListModel.addElement(subject);
            subjectList.setSelectedValue(subject, true);
        });

        // --- Rename ---
        renameButton.addActionListener(e -> {
            Subject selected = subjectList.getSelectedValue();
            if (selected == null) return;
            String input = (String) JOptionPane.showInputDialog(frame, "New name:",
                    "Rename Class", JOptionPane.PLAIN_MESSAGE, null, null, selected.getName());
            if (input == null) return;
            String name = input.trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Class name cannot be empty.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            for (int i = 0; i < subjectListModel.size(); i++) {
                Subject s = subjectListModel.get(i);
                if (s != selected && s.getName().equalsIgnoreCase(name)) {
                    JOptionPane.showMessageDialog(frame, "A class with that name already exists.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            int index = subjectList.getSelectedIndex();
            selected.setName(name);
            subjectListModel.set(index, selected);
        });

        // --- Delete ---
        deleteButton.addActionListener(e -> {
            Subject selected = subjectList.getSelectedValue();
            if (selected == null) return;
            int confirm = JOptionPane.showConfirmDialog(frame,
                    "Delete '" + selected.getName() + "' and all its assignments? This cannot be undone.",
                    "Delete Class", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (confirm != JOptionPane.YES_OPTION) return;
            int index = subjectList.getSelectedIndex();
            subjectListModel.remove(index);
            if (!subjectListModel.isEmpty()) {
                subjectList.setSelectedIndex(Math.max(0, index - 1));
            }
            // If list is now empty, the ListSelectionListener fires with null selection,
            // resetting the table to emptyTableModel and disabling controls.
        });

        // --- Assemble frame ---
        frame.setLayout(new BorderLayout());
        frame.add(sidebarPanel, BorderLayout.WEST);
        frame.add(new JScrollPane(table), BorderLayout.CENTER);
        frame.add(inputPanel, BorderLayout.SOUTH);
        frame.setVisible(true);
    }
}
```

- [ ] **Step 2: Verify compilation**

```bash
ant compile
```

Expected: `BUILD SUCCESSFUL` with no errors.

- [ ] **Step 3: Commit**

```bash
git add src/Main.java
git commit -m "feat: add class/subject sidebar and management (#15)"
```

---

### Task 3: Manual Smoke Test

**Files:** none (verification only)

- [ ] **Step 1: Launch the app**

```bash
ant run
```

Expected: app opens at 750×550 with a dark sidebar on the left. Input panel (Assignment / Due Date / Add / Remove Selected) is disabled. Rename and Delete buttons are disabled.

- [ ] **Step 2: Create a class**

Click `New Class`. Enter "Math". Verify:
- "Math" appears in the sidebar and is highlighted
- Input panel, Rename, and Delete are all now enabled
- Table is empty

- [ ] **Step 3: Add an assignment**

With "Math" selected, type "Homework Ch.1" in the Assignment field, "2026-04-10" in Due Date, click `Add`. Verify the row appears in the table and the fields clear.

- [ ] **Step 4: Verify class isolation**

Click `New Class`, enter "Biology". Verify the table is empty (Biology has no assignments). Click "Math" in the sidebar. Verify "Homework Ch.1" is still in the table.

- [ ] **Step 5: Rename a class**

Select "Biology". Click `Rename`. Change the name to "Bio". Click OK. Verify the sidebar now shows "Bio".

- [ ] **Step 6: Reject duplicate names**

Click `New Class`. Enter "math" (lowercase). Verify an error dialog appears: "A class with that name already exists." Verify no duplicate is added to the sidebar.

- [ ] **Step 7: Delete a class**

Select "Bio". Click `Delete`. Verify the confirmation dialog reads: "Delete 'Bio' and all its assignments? This cannot be undone." Click `Yes`. Verify "Bio" is gone and "Math" is auto-selected with its assignment still present.

- [ ] **Step 8: Delete the last class**

With only "Math" remaining, click `Delete` and confirm. Verify: the sidebar is empty, the table shows no rows, and the input panel and Rename/Delete buttons are all disabled.

- [ ] **Step 9: Commit any fixes found during smoke test**

Only run if changes were needed during testing:

```bash
git add src/Main.java src/Subject.java
git commit -m "fix: address issues found during smoke test (#15)"
```
