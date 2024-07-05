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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainListModel extends AbstractListModel<Object> {

    private final List<Entry> filtered;
    private final Map<String, Entry> entries;

    private boolean showHome = true;
    private boolean showWork = true;
    private boolean showOther = true;

    public MainListModel(List<Entry> entries) {
        this.filtered = new ArrayList<>();
        this.entries = new HashMap<>();
        for (Entry entry : entries) {
            this.entries.put(entry.getKey(), entry);
        }
        filter();
    }

    public int getSize() {
        return filtered.size();
    }

    public Object getElementAt(int index) {
        return filtered.get(index).getDescription();
    }

    public Entry getEntry(int index) {
        return filtered.get(index);
    }

    public void setEntry(Entry entry) {
        entries.put(entry.getKey(), entry);
        filter();
    }

    public void removeEntry(int index) {
        Entry entry = filtered.get(index);
        entries.remove(entry.getKey());
        filter();
    }

    public List<Entry> getEntries() {
        List<Entry> values = new ArrayList<>(entries.values());
        Collections.sort(values);
        return values;
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

    private void filter() {
        filtered.clear();
        for (Entry entry : entries.values()) {
            if (show(entry)) {
                filtered.add(entry);
            }
        }
        Collections.sort(filtered);
        fireContentsChanged(this, 0, filtered.size());
    }

    private boolean show(Entry entry) {
        return switch (entry.getCategory()) {
            case Home -> showHome;
            case Work -> showWork;
            case Other -> showOther;
        };
    }

    public ImportState stateForEntry(Entry otherEntry) {
        for (Entry entry : entries.values()) {
            if (entry.getDescription().equals(otherEntry.getDescription())) {
                if (entry.equals(otherEntry)) {
                    return ImportState.Equal;
                }
                return ImportState.Changed;
            }
        }
        return ImportState.New;
    }
}
