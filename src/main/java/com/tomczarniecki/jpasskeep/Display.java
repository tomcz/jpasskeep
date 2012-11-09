/*
 * Copyright (c) 2005-2010, Thomas Czarniecki
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

import javax.swing.JFrame;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.List;
import java.util.Map;

import static javax.swing.JOptionPane.ERROR_MESSAGE;
import static javax.swing.JOptionPane.INFORMATION_MESSAGE;
import static javax.swing.JOptionPane.YES_NO_OPTION;
import static javax.swing.JOptionPane.YES_OPTION;
import static javax.swing.JOptionPane.showConfirmDialog;
import static javax.swing.JOptionPane.showMessageDialog;

public class Display implements ErrorDisplay {

    private final JFrame frame;
    private final Worker worker;

    public Display(JFrame frame, Worker worker) {
        this.frame = frame;
        this.worker = worker;
    }

    public PasswordDialog createPasswordDialog(String title) {
        return new PasswordDialog(frame, title);
    }

    public SelectDialog createSelectDialog(String title, List<Entry> entries) {
        return new SelectDialog(frame, title, entries);
    }

    public SelectDialog createSelectDialog(String title, List<Entry> entries, Map<String, ImportState> state) {
        return new SelectDialog(frame, title, entries, state);
    }

    public void setClipboardContents(String value) {
        StringSelection selection = new StringSelection(value);
        Clipboard clipboard = frame.getToolkit().getSystemClipboard();
        clipboard.setContents(selection, selection);
    }

    public boolean confirm(String title, String message) {
        int result = showConfirmDialog(frame, message, title, YES_NO_OPTION);
        return result == YES_OPTION;
    }

    public void showInfoMessage(String title, String message) {
        showMessageDialog(frame, message, title, INFORMATION_MESSAGE);
    }

    public void showErrorMessage(String title, String message) {
        showMessageDialog(frame, message, title, ERROR_MESSAGE);
    }

    public void print(List<String> text) {
        worker.runInBackground(new Printer(this, text));
    }

    public void displayError(final String title, final String message) {
        worker.runOnEventLoop(new Runnable() {
            public void run() {
                showErrorMessage(title, message);
            }
        });
    }
}
