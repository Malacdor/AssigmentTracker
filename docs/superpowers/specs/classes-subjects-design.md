# Classes/Subjects Feature Design

**Issue:** [3250Group4/AssigmentTracker#15](https://github.com/3250Group4/AssigmentTracker/issues/15)
**Date:** 2026-04-02
**Status:** Approved

## Summary

Add the ability to create, rename, and delete classes/subjects. Every assignment must belong to a class. Classes are in-memory only (no persistence — tracked separately). Navigation uses a sidebar panel.

## Acceptance Criteria

- Student can create a new class/subject
- Student can rename an existing class/subject
- Student can delete a class/subject (deletes all its assignments after confirmation)
- Every assignment belongs to exactly one class
- The assignment table shows only the assignments for the currently selected class

## Data Model

### New file: `src/Subject.java`

A plain data class with no package declaration (matches the existing `Main.java` convention).

```java
public class Subject {
    private String name;
    private final DefaultTableModel tableModel;
    // constructor, getName(), setName(), getTableModel(), toString()
}
```

- `name` — display name of the class/subject
- `tableModel` — pre-configured `DefaultTableModel` with columns `["Assignment", "Due Date", "Done"]` and the `Boolean` override for column 2
- `toString()` returns `name` so `JList<Subject>` renders correctly without a custom renderer

### Changes to `src/Main.java`

Two new fields tracked in the `createAndShowGUI` scope:

- `DefaultListModel<Subject> subjectListModel` — backing model for the sidebar `JList<Subject>`
- `Subject currentSubject` — reference to the currently selected class; `null` when no class exists

## UI Components

**Window size:** 750×550 (up from 600×450)

**Layout:** `BorderLayout` retained. Changes per region:

| Region | Before | After |
|--------|--------|-------|
| WEST | (empty) | Sidebar panel (~150px wide) |
| CENTER | `JScrollPane(table)` | `JScrollPane(table)` — unchanged |
| SOUTH | Input panel | Input panel — disabled when no class selected |

### Sidebar panel (WEST)

- Dark background (`Color(44, 44, 44)`)
- `JList<Subject>` backed by `subjectListModel`, fills available vertical space via `BorderLayout.CENTER`
- Three buttons pinned to the bottom (`BorderLayout.SOUTH`):
  - `New Class` — always enabled
  - `Rename` — disabled when no class is selected
  - `Delete` — disabled when no class is selected

### Center table

The existing `JTable` is unchanged. Its model is swapped via `table.setModel(subject.getTableModel())` whenever the sidebar selection changes.

### South input panel

Unchanged visually. Disabled (`nameField`, `dateField`, `addButton`, `removeButton` all set non-editable/disabled) when `currentSubject` is `null`.

## Behavior

### Create class

1. User clicks `New Class`
2. `JOptionPane.showInputDialog` prompts for a name
3. Validation: non-empty, not a duplicate (case-insensitive trim)
4. On valid input: create `Subject`, add to `subjectListModel`, select it in the `JList`
5. On cancel or invalid input: no-op (invalid input shows a brief error dialog)

### Rename class

1. User selects a class in the sidebar, clicks `Rename`
2. `JOptionPane.showInputDialog` pre-filled with the current name
3. Same validation as Create (non-empty, no duplicate other than itself)
4. On valid input: call `subject.setName(newName)`, then `subjectListModel.set(index, subject)` to trigger a repaint
5. On cancel: no-op

### Delete class

1. User selects a class, clicks `Delete`
2. `JOptionPane.showConfirmDialog` warns: "Delete '[name]' and all its assignments? This cannot be undone."
3. On confirm: remove `Subject` from `subjectListModel`
4. Post-delete selection:
   - If classes remain: auto-select index `max(0, deletedIndex - 1)`
   - If no classes remain: `currentSubject = null`, disable input panel and table

### Selection change

Handled by a `ListSelectionListener` on the `JList`:

1. Set `currentSubject` to the selected `Subject`
2. Call `table.setModel(currentSubject.getTableModel())`
3. Enable the input panel

## Error Handling

| Scenario | Response |
|----------|----------|
| Blank class name | `JOptionPane.showMessageDialog` — "Class name cannot be empty." |
| Duplicate class name | `JOptionPane.showMessageDialog` — "A class with that name already exists." |
| Add assignment with no class selected | Input panel is disabled — not reachable |
| Delete with nothing selected | Delete button is disabled — not reachable |

## Out of Scope

- Data persistence (tracked in a separate issue)
- Notes (separate from assignments — separate issue #18)
- Sorting/filtering across classes (separate issue #19)
