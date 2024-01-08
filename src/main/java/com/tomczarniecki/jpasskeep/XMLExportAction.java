package com.tomczarniecki.jpasskeep;

import com.tomczarniecki.jpasskeep.crypto.EntryParser;
import com.tomczarniecki.jpasskeep.crypto.JDOMParser;
import org.apache.commons.io.FileUtils;

import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.List;

public class XMLExportAction extends AbstractAction {

    private final EntryParser parser = new JDOMParser(true);
    private final MainListController controller;
    private final Display display;

    public XMLExportAction(MainListController controller, Display display) {
        super("Export XML");
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
        File file = display.showFileChooser("Export XML");
        if (file != null) {
            try {
                FileUtils.writeByteArrayToFile(file, parser.write(entries));
                display.showInfoMessage("Success", "Created " + file);

            } catch (Exception e) {
                e.printStackTrace();
                display.showErrorMessage("Export Error", e.toString());
            }
        }
    }
}
