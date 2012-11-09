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

import com.tomczarniecki.jpasskeep.html.Template;
import com.tomczarniecki.jpasskeep.html.TemplateFactory;

import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class MainDetailsArea implements ListSelectionListener, ListDataListener {

    private boolean showPasswd = true;
    private boolean showNotes = true;

    private final MainListController controller;
    private final TemplateFactory factory;
    private final JEditorPane display;

    public MainDetailsArea(MainListController controller) {
        this.controller = controller;
        this.controller.addListDataListener(this);
        this.controller.addListSelectionListener(this);
        this.factory = new TemplateFactory();
        this.display = new JEditorPane("text/html", "");
        this.display.setEditable(false);
    }

    public boolean isShowNotes() {
        return showNotes;
    }

    public void setShowNotes(boolean showNotes) {
        this.showNotes = showNotes;
        updateDisplay();
    }

    public boolean isShowPasswd() {
        return showPasswd;
    }

    public void setShowPasswd(boolean showPasswd) {
        this.showPasswd = showPasswd;
        updateDisplay();
    }

    public JComponent getDisplay() {
        return new JScrollPane(display);
    }

    public void valueChanged(ListSelectionEvent evt) {
        updateDisplay();
    }

    public void contentsChanged(ListDataEvent e) {
        updateDisplay();
    }

    public void intervalAdded(ListDataEvent e) {
        // valueChanged() will be invoked by the selection of the newly added entry
    }

    public void intervalRemoved(ListDataEvent e) {
        // valueChanged() will be invoked by the selection of the next remaining entry
    }

    private void updateDisplay() {
        if (controller.isEntrySelected()) {
            Entry entry = controller.getSelectedEntry();
            display.setText(format(entry));
            display.setCaretPosition(0);
        } else {
            display.setText("");
        }
    }

    private String format(Entry entry) {
        Template template = factory.entryDetails();
        template.set("showPasswd", showPasswd);
        template.set("showNotes", showNotes);
        template.set("entry", entry);
        return template.toString();
    }
}
