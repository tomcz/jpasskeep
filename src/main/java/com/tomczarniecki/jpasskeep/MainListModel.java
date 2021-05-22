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

import javax.swing.AbstractListModel;
import java.util.ArrayList;
import java.util.List;

public class MainListModel extends AbstractListModel<Object> {

    private List<Integer> filtered;
    private List<Entry> entries;

    private boolean showHome = true;
    private boolean showWork = true;
    private boolean showOther = true;

    public MainListModel(List<Entry> entries) {
        this.filtered = new ArrayList<Integer>();
        this.entries = new ArrayList<Entry>(entries);
        filter();
    }

    public int getSize() {
        return filtered.size();
    }

    public Object getElementAt(int index) {
        return getEntry(index).getDescription();
    }

    public Entry getEntry(int index) {
        return entries.get(getActualIndex(index));
    }

    public void updateEntry(int index, Entry entry) {
        entries.set(getActualIndex(index), entry);
        filter();
    }

    public void appendEntry(Entry entry) {
        entries.add(entry);
        filter();
    }

    public void removeEntry(int index) {
        entries.remove(getActualIndex(index));
        filter();
    }

    public List<Entry> getEntries() {
        return entries;
    }

    public void setShowHome(boolean showHome) {
        this.showHome = showHome;
        filter();
    }

    public boolean isShowHome() {
        return showHome;
    }

    public void setShowWork(boolean showWork) {
        this.showWork = showWork;
        filter();
    }

    public boolean isShowWork() {
        return showWork;
    }

    public void setShowOther(boolean showOther) {
        this.showOther = showOther;
        filter();
    }

    public boolean isShowOther() {
        return showOther;
    }

    private int getActualIndex(int index) {
        return filtered.get(index);
    }

    private void filter() {
        filtered.clear();
        for (int i = 0; i < entries.size(); i++) {
            if (show(entries.get(i))) {
                filtered.add(i);
            }
        }
        fireContentsChanged(this, 0, filtered.size());
    }

    private boolean show(Entry entry) {
        switch (entry.getCategory()) {
            case Home:
                return showHome;
            case Work:
                return showWork;
            case Other:
                return showOther;
            default:
                return true;
        }
    }

    public ImportState stateForEntry(Entry entry) {
        for (Entry modelEntry : entries) {
            if (modelEntry.equals(entry)) {
                return ImportState.Equal;
            }
            if (modelEntry.getDescription().equals(entry.getDescription())) {
                return ImportState.Changed;
            }
        }
        return ImportState.New;
    }
}
