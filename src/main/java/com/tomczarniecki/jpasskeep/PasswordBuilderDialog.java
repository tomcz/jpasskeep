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
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

public class PasswordBuilderDialog extends JDialog implements EventListener<PasswordBuilderDialog.Event> {

    public static enum Event {
        GENERATE, SUBMIT, CANCEL
    }

    private final JCheckBox upperCaseField = new JCheckBox();
    private final JCheckBox lowerCaseField = new JCheckBox();
    private final JCheckBox digitsField = new JCheckBox();
    private final JCheckBox specialCharsField = new JCheckBox();
    private final JCheckBox nonConfusingField = new JCheckBox();
    private final JCheckBox allowRepeatsField = new JCheckBox();
    private final JTextField lengthField = new JTextField("10");

    private final PasswordBuilder builder = new PasswordBuilder();
    private final PasswordBuilderModel model = new PasswordBuilderModel(builder);

    private final JList<Object> list = new JList<>(model);

    public PasswordBuilderDialog(JFrame owner) {
        super(owner, "Password Generator", true);
        initialise(false);
    }

    public PasswordBuilderDialog(JDialog owner) {
        super(owner, "Password Generator", true);
        initialise(true);
    }

    public String display() {
        model.reset();
        setLocationRelativeTo(getOwner());
        setVisible(true);
        int index = list.getSelectedIndex();
        if (index >= 0) {
            return (String) model.getElementAt(index);
        } else {
            return "";
        }
    }

    public void processEvent(Event event) {
        switch (event) {
            case GENERATE:
                generate();
                break;
            case CANCEL:
                list.clearSelection();
                setVisible(false);
                break;
            case SUBMIT:
                setVisible(false);
                break;
        }
    }

    private void initialise(boolean showOK) {
        upperCaseField.setSelected(true);
        lowerCaseField.setSelected(true);
        digitsField.setSelected(true);
        specialCharsField.setSelected(false);
        nonConfusingField.setSelected(true);
        allowRepeatsField.setSelected(true);
        lengthField.setHorizontalAlignment(JTextField.RIGHT);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        getContentPane().add(createDisplay(showOK));
        setResizable(false);
        pack();
    }

    private JPanel createDisplay(boolean showOK) {
        CellConstraints cc = new CellConstraints();

        String rows = "pref,5dlu,pref,5dlu,pref,5dlu,pref,5dlu,pref,5dlu,pref,5dlu,pref,10dlu,pref";
        String cols = "15dlu,5dlu,pref,5dlu,100dlu";
        String checkboxDefaults = "right,default";

        FormLayout layout = new FormLayout(cols, rows);
        PanelBuilder builder = new PanelBuilder(layout);
        builder.setDefaultDialogBorder();

        builder.add(upperCaseField, cc.xy(1, 1, checkboxDefaults));
        builder.addLabel("Upper case", cc.xy(3, 1));
        builder.add(lowerCaseField, cc.xy(1, 3, checkboxDefaults));
        builder.addLabel("Lower case", cc.xy(3, 3));
        builder.add(digitsField, cc.xy(1, 5, checkboxDefaults));
        builder.addLabel("Digits", cc.xy(3, 5));
        builder.add(specialCharsField, cc.xy(1, 7, checkboxDefaults));
        builder.addLabel("Special chars", cc.xy(3, 7));
        builder.add(nonConfusingField, cc.xy(1, 9, checkboxDefaults));
        builder.addLabel("Non-confusing", cc.xy(3, 9));
        builder.add(allowRepeatsField, cc.xy(1, 11, checkboxDefaults));
        builder.addLabel("Allow repeats", cc.xy(3, 11));
        builder.add(lengthField, cc.xy(1, 13));
        builder.addLabel("Length", cc.xy(3, 13));
        builder.add(new JScrollPane(list), cc.xywh(5, 1, 1, 13));
        builder.add(createButtons(showOK), cc.xyw(1, 15, 5));

        return builder.getPanel();
    }

    private JPanel createButtons(boolean showOK) {
        ButtonBarBuilder buttonBar = ButtonBarBuilder.createLeftToRightBuilder();
        buttonBar.addGlue();

        buttonBar.addGridded(new JButton(EventAction.create("Generate", Event.GENERATE, this)));
        buttonBar.addRelatedGap();

        if (showOK) {
            buttonBar.addGridded(new JButton(EventAction.create("OK", Event.SUBMIT, this)));
            buttonBar.addRelatedGap();
        }

        buttonBar.addGridded(new JButton(EventAction.create("Cancel", Event.CANCEL, this)));
        buttonBar.addGlue();

        return buttonBar.getPanel();
    }

    private void generate() {
        try {
            int length = parseLength();
            String alphabet = buildAlphabet();
            builder.setLength(length);
            builder.setAlphabet(alphabet);
            builder.setPermitRepeats(allowRepeatsField.isSelected());
            model.generate();

        } catch (PasswordBuilderException e) {
            JOptionPane.showMessageDialog(getOwner(), e.getMessage(), "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private int parseLength() throws PasswordBuilderException {
        try {
            return Integer.parseInt(lengthField.getText());
        } catch (NumberFormatException e) {
            throw new PasswordBuilderException("Length must be a number!");
        }
    }

    private String buildAlphabet() throws PasswordBuilderException {
        AlphabetBuilder generator = new AlphabetBuilder();
        generator.setUpperCase(upperCaseField.isSelected());
        generator.setLowerCase(lowerCaseField.isSelected());
        generator.setDigits(digitsField.isSelected());
        generator.setSpecialChars(specialCharsField.isSelected());
        generator.setNonConfusing(nonConfusingField.isSelected());
        String alphabet = generator.buildAlphabet();
        if (StringUtils.isBlank(alphabet)) {
            throw new PasswordBuilderException("Please select some password characters!");
        }
        return alphabet;
    }
}
