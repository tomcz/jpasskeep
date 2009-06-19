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
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionListener;
import java.beans.EventHandler;
import java.io.File;

public class PasswordDialog extends JDialog {

    private JTextField fileField;
    private JPasswordField passField;
    private JFileChooser chooser;

    private boolean showOpen;
    private boolean success;

    public PasswordDialog(JFrame owner, String title) {
        super(owner, title, true);
        this.fileField = new JTextField();
        this.passField = new JPasswordField();
        this.passField.addActionListener(EventHandler.create(ActionListener.class, this, "clickSubmit"));
        this.chooser = new JFileChooser();
        createDisplay();
        setResizable(false);
    }

    public boolean showOpenDialog() {
        return showDialog(null, true);
    }

    public boolean showSaveDialog() {
        return showDialog(null, false);
    }

    public boolean showDialog(File file, boolean showOpen) {
        this.showOpen = showOpen;
        this.success = false;
        setupFields((file != null) ? file.getAbsolutePath() : "");
        centerOnOwner(getOwner());
        setVisible(true);
        return processFields();
    }

    public File getFile() {
        String fileName = StringUtils.trimToNull(fileField.getText());
        return (fileName != null) ? new File(fileName) : null;
    }

    public char[] getPassword() {
        char[] password = passField.getPassword();
        if ((password != null) && (password.length > 0)) {
            return password;
        }
        return null;
    }

    public void clickSelect() {
        File file = getFile();
        if (file != null) {
            chooser.setSelectedFile(file);
        }
        int result = showOpen ? chooser.showOpenDialog(this) : chooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            fileField.setText(chooser.getSelectedFile().getAbsolutePath());
            passField.requestFocusInWindow();
        }
    }

    public void clickSubmit() {
        success = true;
        setVisible(false);
    }

    public void clickCancel() {
        setVisible(false);
    }

    private void createDisplay() {
        CellConstraints cc = new CellConstraints();
        FormLayout layout = new FormLayout("pref,5dlu,100dlu,5dlu,pref", "pref,5dlu,pref,5dlu,pref,10dlu,pref");
        PanelBuilder builder = new PanelBuilder(layout);
        builder.setDefaultDialogBorder();
        builder.addLabel("Please select a file and enter its password:", cc.xyw(1, 1, 5));
        builder.addLabel("File Name", cc.xy(1, 3));
        builder.add(fileField, cc.xy(3, 3));
        builder.add(createButton("Select", "clickSelect"), cc.xy(5, 3));
        builder.addLabel("Password", cc.xy(1, 5));
        builder.add(passField, cc.xy(3, 5));
        builder.add(createButtonPanel(), cc.xyw(1, 7, 5));
        getContentPane().add(builder.getPanel());
        pack();
    }

    private JPanel createButtonPanel() {
        ButtonBarBuilder buttonBar = ButtonBarBuilder.createLeftToRightBuilder();
        buttonBar.addGlue();
        buttonBar.addGridded(createButton("OK", "clickSubmit"));
        buttonBar.addRelatedGap();
        buttonBar.addGridded(createButton("Cancel", "clickCancel"));
        buttonBar.addGlue();
        return buttonBar.getPanel();
    }

    private JButton createButton(String title, String method) {
        JButton button = new JButton(title);
        button.addActionListener(EventHandler.create(ActionListener.class, this, method));
        return button;
    }

    private void centerOnOwner(Window owner) {
        if (owner != null) {
            setLocationRelativeTo(owner);
        } else {
            Dimension paneSize = getSize();
            Dimension screenSize = getToolkit().getScreenSize();
            setLocation((screenSize.width - paneSize.width) / 2, (screenSize.height - paneSize.height) / 2);
        }
    }

    private void setupFields(String fileName) {
        fileField.setText(fileName);
        passField.setText("");
        passField.requestFocusInWindow();
    }

    private boolean processFields() {
        if (success) {
            if (getFile() == null) {
                return false;
            }
            if (getPassword() == null) {
                return false;
            }
            return true;
        }
        return false;
    }
}
