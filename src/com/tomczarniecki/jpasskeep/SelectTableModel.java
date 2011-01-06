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

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SelectTableModel extends AbstractTableModel {

    private static final String[] DEFAULT_COLUMNS = {"Select", "Entry", "Category"};
    private static final String[] COLUMNS_FOR_IMPORT = {"Select", "Entry", "Category", ""};

    private List<ModelEntry> entries;
    private String[] columns;

    public SelectTableModel(List<Entry> entries) {
        this.columns = DEFAULT_COLUMNS;
        this.entries = new ArrayList<ModelEntry>(entries.size());
        for (Entry entry : entries) {
            ModelEntry model = new ModelEntry();
            model.entry = entry;
            model.selected = true;
            this.entries.add(model);
        }
    }

    public SelectTableModel(List<Entry> entries, Map<String, ImportState> stateForEntries) {
        this(entries);
        this.columns = COLUMNS_FOR_IMPORT;
        for (ModelEntry model : this.entries) {
            model.state = stateForEntries.get(model.entry.getDescription());
        }
    }

    public int getRowCount() {
        return entries.size();
    }

    public int getColumnCount() {
        return columns.length;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        ModelEntry model = entries.get(rowIndex);
        if (isSelection(columnIndex)) {
            return model.selected;
        }
        if (isCategory(columnIndex)) {
            return model.entry.getCategoryName();
        }
        if (isDescription(columnIndex)) {
            return model.entry.getDescription();
        }
        return model.state.name();
    }

    public String getColumnName(int column) {
        return columns[column];
    }

    public Class<?> getColumnClass(int columnIndex) {
        return isSelection(columnIndex) ? Boolean.class : String.class;
    }

    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return isSelection(columnIndex);
    }

    private boolean isSelection(int columnIndex) {
        return (columnIndex == 0);
    }

    private boolean isDescription(int columnIndex) {
        return (columnIndex == 1);
    }

    private boolean isCategory(int columnIndex) {
        return (columnIndex == 2);
    }

    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        ModelEntry model = entries.get(rowIndex);
        model.selected = (Boolean) aValue;
        fireTableCellUpdated(rowIndex, columnIndex);
    }

    public List<Entry> getSelectedEntries() {
        List<Entry> selectedEntries = new ArrayList<Entry>();
        for (ModelEntry model : entries) {
            if (model.selected) {
                selectedEntries.add(model.entry);
            }
        }
        return selectedEntries;
    }

    public void toggle(Category category, boolean selected) {
        for (ModelEntry entry : entries) {
            if (entry.entry.getCategory().equals(category)) {
                entry.selected = selected;
            }
        }
        fireTableDataChanged();
    }

    private class ModelEntry {
        ImportState state;
        boolean selected;
        Entry entry;
    }
}
