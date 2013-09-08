package com.tomczarniecki.jpasskeep;

import au.com.bytecode.opencsv.CSVReader;

import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.commons.io.IOUtils.closeQuietly;

public class CSVImportAction extends AbstractAction {

    private final MainListController controller;
    private final Display display;

    public CSVImportAction(MainListController controller, Display display) {
        super("Import CSV");
        this.controller = controller;
        this.display = display;
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        File file = display.showFileChooser("Import CSV");
        if (file != null) {
            try {
                List<Entry> fileEntries = readEntries(file);
                List<Entry> selectedEntries = selectEntries(fileEntries);
                appendEntries(selectedEntries);

            } catch (Exception e) {
                e.printStackTrace();
                display.showErrorMessage("Export Error", e.toString());
            }
        }
    }

    private List<Entry> readEntries(File file) throws Exception {
        List<Entry> entries = new ArrayList<Entry>();
        for (String[] line : readLines(file)) {
            Entry entry = new Entry();
            entry.setDescription(line[0]);
            entry.setUsername(line[1]);
            entry.setPassword(line[2]);
            entry.setNotes(line[3]);
            entries.add(entry);
        }
        return entries;
    }

    private List<String[]> readLines(File file) throws Exception {
        CSVReader reader = null;
        try {
            reader = new CSVReader(new FileReader(file), ',');
            return reader.readAll();
        } finally {
            closeQuietly(reader);
        }
    }

    private List<Entry> selectEntries(List<Entry> entries) {
        Map<String, ImportState> stateForEntries = findStateForEntries(entries);
        SelectDialog dialog = display.createSelectDialog("Select Entries to Import", entries, stateForEntries);
        return dialog.selectEntries();
    }

    private Map<String, ImportState> findStateForEntries(List<Entry> entries) {
        Map<String, ImportState> stateForEntries = new HashMap<String, ImportState>();
        for (Entry entry : entries) {
            stateForEntries.put(entry.getDescription(), controller.stateForEntry(entry));
        }
        return stateForEntries;
    }

    private void appendEntries(List<Entry> entries) {
        for (Entry entry : entries) {
            controller.appendEntry(entry);
        }
    }
}
