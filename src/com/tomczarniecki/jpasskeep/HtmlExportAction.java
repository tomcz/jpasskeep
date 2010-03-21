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

import com.tomczarniecki.jpasskeep.crypto.GibberishAESCrypt;
import com.tomczarniecki.jpasskeep.crypto.LightGibberishAESCrypt;
import com.tomczarniecki.jpasskeep.html.Template;
import com.tomczarniecki.jpasskeep.html.TemplateFactory;
import org.apache.commons.io.FileUtils;

import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.List;

import static com.tomczarniecki.jpasskeep.resources.Resource.loadResource;

public class HtmlExportAction extends AbstractAction {

    private final MainListController controller;
    private final TemplateFactory factory;
    private final Display display;

    public HtmlExportAction(MainListController controller, Display display) {
        super("Export HTML");
        this.factory = new TemplateFactory();
        this.controller = controller;
        this.display = display;
    }

    public void actionPerformed(ActionEvent evt) {
        SelectDialog selection = display.createSelectDialog("Select Entries to Export", controller.getEntries());
        List<Entry> entries = selection.selectEntries();
        if (entries.isEmpty()) {
            return; // user cancelled
        }
        PasswordDialog dialog = display.createPasswordDialog("Export HTML");
        if (dialog.showSaveDialog()) {
            try {
                File file = dialog.getFile();

                String plainText = createEntriesDiv(entries);
                GibberishAESCrypt crypto = new LightGibberishAESCrypt();
                String cipherText = crypto.encrypt(plainText, dialog.getPassword());

                String entriesPage = createEntriesPage(cipherText);
                FileUtils.writeStringToFile(file, entriesPage, "UTF-8");

                display.showInfoMessage("Success", "Created " + file);

            } catch (Exception e) {
                e.printStackTrace();
                display.showErrorMessage("Export Error", e.toString());
            }
        }
    }

    private String createEntriesDiv(List<Entry> entries) {
        Template template = factory.entriesDiv();
        template.set("entries", entries);
        return template.toString();
    }

    private String createEntriesPage(String cipherText) throws Exception {
        Template template = factory.entriesPage();
        template.set("jquery", loadResource("jquery-1.3.2-min.js"));
        template.set("gibberish", loadResource("gibberish-aes.min.js"));
        template.set("jpasskeep", loadResource("jpasskeep-min.js"));
        template.set("css", loadResource("jpasskeep-min.css"));
        template.set("cipherText", cipherText);
        return template.toString();
    }
}
