package com.tomczarniecki.jpasskeep;

import com.tomczarniecki.jpasskeep.crypto.EntryParser;
import com.tomczarniecki.jpasskeep.crypto.JDOMParser;
import org.apache.commons.io.FileUtils;

import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XMLImportAction extends AbstractAction {

    private final EntryParser parser = new JDOMParser();
    private final MainListController controller;
    private final Display display;

    public XMLImportAction(MainListController controller, Display display) {
        super("Import XML");
        this.controller = controller;
        this.display = display;
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        File file = display.showFileChooser("Import XML");
        if (file != null) {
            try {
                List<Entry> fileEntries = parser.read(FileUtils.readFileToByteArray(file));
                List<Entry> selectedEntries = selectEntries(fileEntries);
                appendEntries(selectedEntries);

            } catch (Exception e) {
                e.printStackTrace();
                display.showErrorMessage("Export Error", e.toString());
            }
        }
    }

    private List<Entry> selectEntries(List<Entry> entries) {
        Map<String, ImportState> stateForEntries = findStateForEntries(entries);
        SelectDialog dialog = display.createSelectDialog("Select Entries to Import", entries, stateForEntries);
        return dialog.selectEntries();
    }

    private Map<String, ImportState> findStateForEntries(List<Entry> entries) {
        Map<String, ImportState> stateForEntries = new HashMap<>();
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
