package com.tomczarniecki.jpasskeep;

import com.jgoodies.forms.builder.ButtonBarBuilder;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import java.awt.BorderLayout;
import java.awt.Container;

public class AssignCategoriesDialog extends JDialog implements EventListener<AssignCategoriesDialog.Event> {
    public enum Event {
        SUBMIT, CANCEL;
    }

    private final MainListController controller;
    private final AssignCategoriesModel model;
    private boolean success;

    public AssignCategoriesDialog(JFrame owner, MainListController controller) {
        super(owner, "Assign Categories", true);
        this.model = new AssignCategoriesModel(controller.getEntries());
        this.controller = controller;

        var table = new JTable(this.model);
        var columnModel = table.getColumnModel();
        var categoryColumn = columnModel.getColumn(1);
        var comboBox = new JComboBox<>(Category.values());
        categoryColumn.setCellEditor(new DefaultCellEditor(comboBox));

        ButtonBarBuilder buttonBar = ButtonBarBuilder.createLeftToRightBuilder();
        buttonBar.setDefaultDialogBorder();
        buttonBar.addGlue();
        buttonBar.addGridded(new JButton(EventAction.create("OK", AssignCategoriesDialog.Event.SUBMIT, this)));
        buttonBar.addRelatedGap();
        buttonBar.addGridded(new JButton(EventAction.create("Cancel", AssignCategoriesDialog.Event.CANCEL, this)));
        buttonBar.addGlue();

        Container cc = getContentPane();
        cc.setLayout(new BorderLayout());
        cc.add(new JScrollPane(table), BorderLayout.CENTER);
        cc.add(buttonBar.getPanel(), BorderLayout.SOUTH);
        setResizable(false);
        pack();
    }

    public void assign() {
        success = false;
        setLocationRelativeTo(getOwner());
        setVisible(true);
        // setVisible(true) blocks until set to false
        if (success) {
            var entries = controller.getEntries();
            for (int i = 0; i < entries.size(); i++) {
                var entry = entries.get(i);
                var category = model.getCategory(i);
                if (entry.getCategory() != category) {
                    entry.setCategory(category);
                    controller.updateEntryAt(i, entry);
                }
            }
        }
    }

    @Override
    public void processEvent(Event event) {
        switch (event) {
            case SUBMIT:
                success = true;
                setVisible(false);
                break;
            case CANCEL:
                setVisible(false);
                break;
        }
    }
}
