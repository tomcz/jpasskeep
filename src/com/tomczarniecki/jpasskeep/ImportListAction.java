/*
 * Copyright (c) 2005-2009, Thomas Czarniecki
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *  * Neither the name of JPasskeep, Thomas Czarniecki, tomczarniecki.com nor
 *    the names of its contributors may be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.tomczarniecki.jpasskeep;

import com.tomczarniecki.jpasskeep.crypto.CryptoException;
import com.tomczarniecki.jpasskeep.crypto.CryptoUtils;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImportListAction extends AbstractAction {

    private JFrame parent;
    private PasswordDialog dialog;
    private MainListController controller;

    public ImportListAction(MainListController controller, JFrame parent) {
        super("Import List");
        this.parent = parent;
        this.dialog = new PasswordDialog(parent, "Import List");
        this.controller = controller;
    }

    public void actionPerformed(ActionEvent evt) {
        if (dialog.showOpenDialog()) {
            try {
                List<Entry> fileEntries = CryptoUtils.decrypt(dialog.getFile(), dialog.getPassword());
                List<Entry> selectedEntries = selectEntries(fileEntries);
                appendEntries(selectedEntries);
            } catch (CryptoException e) {
                handleError("Import Error", "Invalid username/password", e);
            } catch (Exception e) {
                handleError("Import Error", e.toString(), e);
            }
        }
    }

    private void handleError(String title, String message, Exception error) {
        error.printStackTrace();
        JOptionPane.showMessageDialog(parent, message, title, JOptionPane.ERROR_MESSAGE);
    }

    private List<Entry> selectEntries(List<Entry> entries) {
        Map<String, ImportState> stateForEntries = findStateForEntries(entries);
        SelectDialog dialog = new SelectDialog(parent, "Select Entries to Import", entries, stateForEntries);
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
