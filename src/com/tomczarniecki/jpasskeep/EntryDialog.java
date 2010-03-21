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
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import org.apache.commons.lang.StringUtils;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.awt.Frame;

public class EntryDialog extends JDialog implements EventListener<EntryDialog.Event> {

    public static enum Event {
        GENERATE, SAVE, CANCEL
    }

    private final PasswordBuilderDialog dialog;
    private final JTextField description;
    private final JComboBox category;
    private final JTextField username;
    private final JTextField password;
    private final JTextArea notes;

    private boolean success = false;

    public EntryDialog(Frame owner, String title) {
        super(owner, title, true);

        dialog = new PasswordBuilderDialog(this);

        description = new JTextField();
        category = new JComboBox(Category.values());
        username = new JTextField();
        password = new JTextField();
        notes = new JTextArea();

        getContentPane().add(createDisplay());
        setResizable(false);
        pack();
    }

    public Entry display(Entry entry) {
        success = false;

        setFieldsFromEntry(entry);
        setLocationRelativeTo(getOwner());
        setVisible(true);

        // setVisible will block until its false
        return success ? getEntryFromFields() : null;
    }

    public void processEvent(Event event) {
        switch (event) {
            case GENERATE:
                generate();
                break;
            case SAVE:
                success = true;
                setVisible(false);
                break;
            case CANCEL:
                setVisible(false);
                break;
        }
    }

    private void generate() {
        String pwd = dialog.display();
        if (StringUtils.isNotBlank(pwd)) {
            password.setText(pwd);
        }
    }

    private JPanel createDisplay() {
        CellConstraints cc = new CellConstraints();

        String cols = "pref,5dlu,100dlu,5dlu,pref";
        String rows = "pref,5dlu,pref,5dlu,pref,5dlu,pref,10dlu,50dlu,10dlu,pref";

        FormLayout layout = new FormLayout(cols, rows);
        PanelBuilder builder = new PanelBuilder(layout);
        builder.setDefaultDialogBorder();

        category.setEditable(false);
        notes.setLineWrap(true);
        notes.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(notes);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        builder.addLabel("Description", cc.xy(1, 1));
        builder.add(description, cc.xyw(3, 1, 3));
        builder.addLabel("Category", cc.xy(1, 3));
        builder.add(category, cc.xyw(3, 3, 3));
        builder.addLabel("Username", cc.xy(1, 5));
        builder.add(username, cc.xyw(3, 5, 3));
        builder.addLabel("Password", cc.xy(1, 7));
        builder.add(password, cc.xy(3, 7));
        builder.add(new JButton(EventAction.create("Generate", Event.GENERATE, this)), cc.xy(5, 7));
        builder.add(scrollPane, cc.xyw(1, 9, 5, "fill,fill"));
        builder.add(createButtonPanel(), cc.xyw(1, 11, 5));

        return builder.getPanel();
    }

    private JPanel createButtonPanel() {
        ButtonBarBuilder buttonBar = ButtonBarBuilder.createLeftToRightBuilder();
        buttonBar.addGlue();
        buttonBar.addGridded(new JButton(EventAction.create("Save", Event.SAVE, this)));
        buttonBar.addRelatedGap();
        buttonBar.addGridded(new JButton(EventAction.create("Cancel", Event.CANCEL, this)));
        buttonBar.addGlue();
        return buttonBar.getPanel();
    }

    private void setFieldsFromEntry(Entry entry) {
        if (entry != null) {
            description.setText(entry.getDescription());
            category.setSelectedItem(entry.getCategory());
            username.setText(entry.getUsername());
            password.setText(entry.getPassword());
            notes.setText(entry.getNotes());
        } else {
            description.setText(null);
            category.setSelectedIndex(0);
            username.setText(null);
            password.setText(null);
            notes.setText(null);
        }
    }

    private Entry getEntryFromFields() {
        Entry entry = new Entry();
        entry.setDescription(description.getText());
        entry.setCategory((Category) category.getSelectedItem());
        entry.setUsername(username.getText());
        entry.setPassword(password.getText());
        entry.setNotes(notes.getText());
        return entry;
    }
}
