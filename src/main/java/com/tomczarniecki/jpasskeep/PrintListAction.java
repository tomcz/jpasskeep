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

import org.apache.commons.lang.StringUtils;

import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PrintListAction extends AbstractAction {

    private final MainListController controller;
    private final Display display;

    public PrintListAction(MainListController controller, Display display) {
        super("Print List");
        this.controller = controller;
        this.display = display;
    }

    public void actionPerformed(ActionEvent e) {
        SelectDialog selection = display.createSelectDialog("Select Entries to Print", controller.getEntries());
        List<Entry> entries = selection.selectEntries();
        if (entries.isEmpty()) {
            return; // user cancelled
        }
        List<String> text = new ArrayList<String>();
        for (Entry entry : entries) {
            formatEntry(text, entry);
        }
        display.print(text);
    }

    private void formatEntry(List<String> text, Entry entry) {
        text.add("DESCRIPTION: " + entry.getDescription());
        text.add("CATEGORY: " + entry.getCategoryName());
        text.add("USERNAME: " + entry.getUsername());
        text.add("PASSWORD: " + entry.getPassword());
        if (StringUtils.isNotBlank(entry.getNotes())) {
            text.add("NOTES:");
            text.addAll(Arrays.asList(StringUtils.split(entry.getNotes(), "\n\r\f")));
        }
        text.add(StringUtils.repeat("-", 30));
    }
}
