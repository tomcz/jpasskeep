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

import com.jgoodies.forms.builder.ButtonBarBuilder;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableColumnModel;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionListener;
import java.beans.EventHandler;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SelectDialog extends JDialog {

    private SelectTableModel model;
    private boolean success;

    public SelectDialog(JFrame parent, String title, List<Entry> entries) {
        super(parent, title, true);
        model = new SelectTableModel(entries);
        setupView();
    }

    public SelectDialog(JFrame parent, String title, List<Entry> entries, Map<String, ImportState> stateForEntries) {
        super(parent, title, true);
        model = new SelectTableModel(entries, stateForEntries);
        setupView();
    }

    private void setupView() {
        Container cc = getContentPane();
        cc.setLayout(new BorderLayout());
        cc.add(createDisplay(), BorderLayout.CENTER);
        cc.add(createButtons(), BorderLayout.SOUTH);
        setResizable(false);
        pack();
    }

    public List<Entry> selectEntries() {
        success = false;
        setLocationRelativeTo(getOwner());
        setVisible(true);
        if (success) {
            return model.getSelectedEntries();
        } else {
            return new ArrayList<Entry>(0);
        }
    }

    public void save() {
        success = true;
        setVisible(false);
    }

    public void cancel() {
        setVisible(false);
    }

    private JScrollPane createDisplay() {
        JTable table = new JTable(model);
        TableColumnModel columnModel = table.getColumnModel();
        columnModel.getColumn(0).setMaxWidth(100);
        columnModel.getColumn(2).setMaxWidth(200);
        if (columnModel.getColumnCount() > 3) {
            columnModel.getColumn(3).setMaxWidth(100);
        }
        return new JScrollPane(table);
    }

    private JPanel createButtons() {
        ButtonBarBuilder buttonBar = ButtonBarBuilder.createLeftToRightBuilder();
        buttonBar.setDefaultDialogBorder();
        buttonBar.addGlue();

        JButton selectAll = new JButton("Select All");
        selectAll.addActionListener(EventHandler.create(ActionListener.class, model, "selectAll"));
        buttonBar.addGridded(selectAll);

        buttonBar.addRelatedGap();

        JButton deselectAll = new JButton("Clear All");
        deselectAll.addActionListener(EventHandler.create(ActionListener.class, model, "deselectAll"));
        buttonBar.addGridded(deselectAll);

        buttonBar.addUnrelatedGap();

        JButton saveButton = new JButton("OK");
        saveButton.addActionListener(EventHandler.create(ActionListener.class, this, "save"));
        buttonBar.addGridded(saveButton);

        buttonBar.addRelatedGap();

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(EventHandler.create(ActionListener.class, this, "cancel"));
        buttonBar.addGridded(cancelButton);

        buttonBar.addGlue();
        return buttonBar.getPanel();
    }
}
