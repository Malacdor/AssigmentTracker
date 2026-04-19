import javax.swing.table.DefaultTableModel;

public class Subject {
    private String name;
    private final DefaultTableModel tableModel;

    public Subject(String name) {
        this.name = name;
        this.tableModel = new DefaultTableModel(
                new String[]{"Assignment", "Due Date", "Done", "Notes"}, 0) {
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
