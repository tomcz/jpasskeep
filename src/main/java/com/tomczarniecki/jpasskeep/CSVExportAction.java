package com.tomczarniecki.jpasskeep;

import au.com.bytecode.opencsv.CSVWriter;

import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileWriter;
import java.util.List;

import static org.apache.commons.io.IOUtils.closeQuietly;

public class CSVExportAction extends AbstractAction {

    private final MainListController controller;
    private final Display display;

    public CSVExportAction(MainListController controller, Display display) {
        super("Export CSV");
        this.controller = controller;
        this.display = display;
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        SelectDialog selection = display.createSelectDialog("Select Entries to Export", controller.getEntries());
        List<Entry> entries = selection.selectEntries();
        if (entries.isEmpty()) {
            return; // user cancelled
        }
        File file = display.showFileChooser("Export CSV");
        if (file != null) {
            try {
                exportToCSV(entries, file);
                display.showInfoMessage("Success", "Created " + file);

            } catch (Exception e) {
                e.printStackTrace();
                display.showErrorMessage("Export Error", e.toString());
            }
        }
    }

    private void exportToCSV(List<Entry> entries, File file) throws Exception {
        CSVWriter writer = null;
        try {
            writer = new CSVWriter(new FileWriter(file), ',');
            for (Entry entry : entries) {
                writer.writeNext(array(entry.getDescription(), entry.getUsername(),
                        entry.getPassword(), entry.getCategoryName(), entry.getNotes()));
            }
        } finally {
            closeQuietly(writer);
        }
    }

    private String[] array(String... args) {
        return args;
    }
}
