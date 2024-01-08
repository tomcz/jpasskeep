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

import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionListener;
import java.awt.event.MouseListener;
import java.util.List;

public class MainListController {

    private final JList<Object> display;
    private final MainListModel model;
    private boolean dirty = false;

    public MainListController(List<Entry> entries) {
        model = new MainListModel(entries);
        display = new JList<>(model);
        display.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        display.setLayoutOrientation(JList.VERTICAL_WRAP);
        display.setVisibleRowCount(-1);
    }

    public boolean isDirty() {
        return dirty;
    }

    public List<Entry> getEntries() {
        return model.getEntries();
    }

    public JComponent getDisplay() {
        return new JScrollPane(display);
    }

    public boolean isEntrySelected() {
        return (getSelectedIndex() > -1) && (model.getSize() > 0);
    }

    public Entry getSelectedEntry() {
        return model.getEntry(getSelectedIndex());
    }

    public void appendEntry(Entry entry) {
        dirty = true;
        model.appendEntry(entry);
        refreshList(model.getSize() - 1);
    }

    public void updateSelectedEntry(Entry update) {
        dirty = true;
        int index = getSelectedIndex();
        model.updateEntry(index, update);
        refreshList(index);
    }

    public void updateEntryAt(int index, Entry entry) {
        dirty = true;
        model.updateEntry(index, entry);
        refreshList(index);
    }

    public void removeSelectedEntry() {
        dirty = true;
        int index = getSelectedIndex();
        model.removeEntry(index);
        if (model.getSize() == index) {
            refreshList(index - 1);
        } else {
            refreshList(index);
        }
    }

    public void setShowHome(boolean showHome) {
        refreshList(0);
        model.setShowHome(showHome);
    }

    public boolean isShowHome() {
        return model.isShowHome();
    }

    public void setShowWork(boolean showWork) {
        refreshList(0);
        model.setShowWork(showWork);
    }

    public boolean isShowWork() {
        return model.isShowWork();
    }

    public void setShowOther(boolean showOther) {
        refreshList(0);
        model.setShowOther(showOther);
    }

    public boolean isShowOther() {
        return model.isShowOther();
    }

    public void addListSelectionListener(ListSelectionListener listener) {
        display.getSelectionModel().addListSelectionListener(listener);
    }

    public void addListDataListener(ListDataListener listener) {
        model.addListDataListener(listener);
    }

    public void addMouseListener(MouseListener listener) {
        display.addMouseListener(listener);
    }

    private int getSelectedIndex() {
        return display.getSelectedIndex();
    }

    private void refreshList(int index) {
        display.setSelectedIndex(index);
        display.ensureIndexIsVisible(index);
    }

    public ImportState stateForEntry(Entry entry) {
        return model.stateForEntry(entry);
    }
}
