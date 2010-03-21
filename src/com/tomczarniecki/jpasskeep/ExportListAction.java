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

import com.tomczarniecki.jpasskeep.crypto.EntryCipher;

import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.List;

public class ExportListAction extends AbstractAction {

    private final MainListController controller;
    private final Display display;
    private final EntryCipher cipher;

    public ExportListAction(MainListController controller, Display display, EntryCipher cipher) {
        super("Export List");
        this.controller = controller;
        this.display = display;
        this.cipher = cipher;
    }

    public void actionPerformed(ActionEvent evt) {
        SelectDialog selection = display.createSelectDialog("Select Entries to Export", controller.getEntries());
        List<Entry> entries = selection.selectEntries();
        if (entries.isEmpty()) {
            return; // user cancelled
        }
        PasswordDialog dialog = display.createPasswordDialog("Export List");
        if (dialog.showSaveDialog()) {
            try {
                File file = dialog.getFile();
                cipher.encrypt(entries, file, dialog.getPassword());
                display.showInfoMessage("Success", "Created " + file);

            } catch (Exception e) {
                e.printStackTrace();
                display.showErrorMessage("Export Error", e.toString());
            }
        }
    }
}
