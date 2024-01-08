package com.tomczarniecki.jpasskeep;

import javax.swing.table.AbstractTableModel;
import java.util.List;

public class AssignCategoriesModel extends AbstractTableModel {

    private static class Pair {
        String name;
        Category category;
    }

    private final static String[] columns = {"Name", "Category"};
    private final List<Pair> entries;

    public AssignCategoriesModel(List<Entry> entries) {
        this.entries = entries.stream().map(AssignCategoriesModel::toPair).toList();
    }

    private static Pair toPair(Entry entry) {
        var pair = new Pair();
        pair.name = entry.getDescription();
        pair.category = entry.getCategory();
        return pair;
    }

    @Override
    public String getColumnName(int column) {
        return columns[column];
    }

    @Override
    public int getRowCount() {
        return entries.size();
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public Object getValueAt(int row, int col) {
        var pair = entries.get(row);
        return (col == 0) ? pair.name : pair.category;
    }

    public Category getCategory(int row) {
        return entries.get(row).category;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return getValueAt(0, columnIndex).getClass();
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex == 1;
    }

    @Override
    public void setValueAt(Object value, int rowIndex, int columnIndex) {
        if (columnIndex == 1) {
            entries.get(rowIndex).category = (Category) value;
            fireTableCellUpdated(rowIndex, columnIndex);
        }
    }
}
