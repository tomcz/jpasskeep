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

import com.tomczarniecki.jpasskeep.crypto.CryptoException;
import com.tomczarniecki.jpasskeep.crypto.EntryCipher;
import org.apache.commons.lang.SystemUtils;

import javax.swing.JFrame;
import javax.swing.UIManager;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static javax.swing.JOptionPane.ERROR_MESSAGE;
import static javax.swing.JOptionPane.NO_OPTION;
import static javax.swing.JOptionPane.YES_NO_CANCEL_OPTION;
import static javax.swing.JOptionPane.YES_OPTION;
import static javax.swing.JOptionPane.showConfirmDialog;
import static javax.swing.JOptionPane.showMessageDialog;

public class Main {

    private File passFile;
    private char[] password;

    private MainFrame frame;
    private MainListController controller;

    private final Worker worker = new Worker();
    private final EntryCipher cipher = new EntryCipher();

    public static void main(String[] args) {
        if (SystemUtils.IS_OS_MAC_OSX) {
            System.setProperty("apple.laf.useScreenMenuBar", "false");
            System.setProperty("apple.eawt.quitStrategy", "CLOSE_ALL_WINDOWS");
        }
        try {
            UIManager.setLookAndFeel("com.jgoodies.looks.plastic.PlasticXPLookAndFeel");
        } catch (Exception e) {
            // not to worry, can still use platform default L&F
        }
        Main application = new Main();
        application.start(args);
    }

    private void start(final String[] args) {
        worker.runOnEventLoop(new Runnable() {
            public void run() {
                initialise(args);
                createAndShowGUI();
            }
        });
    }

    private void initialise(String[] args) {
        if (args.length == 0) {
            passFile = new File(SystemUtils.USER_HOME, ".jpasskeep");
        } else {
            passFile = new File(args[0]);
        }
        PasswordDialog dialog = new PasswordDialog(null, "Password Keeper");
        if (dialog.showDialog(passFile, true)) {
            passFile = dialog.getFile();
            password = dialog.getPassword();
            controller = new MainListController(loadEntries());
        } else {
            System.exit(0);
        }
    }

    private void createAndShowGUI() {
        PreferenceSetter prefs = new PreferenceSetter(MainFrame.class);

        frame = new MainFrame(controller, cipher, worker, prefs);
        Display display = frame.getDisplay();

        RightClickMenu menu = new RightClickMenu();
        menu.addAction(new CopyUsernameAction(controller, display));
        menu.addAction(new CopyPasswordAction(controller, display));

        controller.addMouseListener(menu);

        QuitHandler quit = new CompositeQuitHandler(prefs, new SaveEntriesOnQuit());

        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowCloseListener(quit));
        frame.setVisible(true);
    }

    private List<Entry> loadEntries() {
        try {
            if (passFile.exists()) {
                return cipher.decrypt(passFile, password);
            } else {
                return new ArrayList<Entry>();
            }
        } catch (CryptoException e) {
            handleError("Input Error", "Invalid password", e);

        } catch (Exception e) {
            handleError("Unexpected Error", e.toString(), e);
        }
        System.exit(1);
        return null;
    }

    private void saveEntries() {
        try {
            cipher.encrypt(controller.getEntries(), passFile, password);
        } catch (Exception e) {
            handleError("Save Error", e.toString(), e);
        }
    }

    private void handleError(String title, String message, Exception error) {
        error.printStackTrace();
        showMessageDialog(frame, message, title, ERROR_MESSAGE);
    }

    private class SaveEntriesOnQuit implements QuitHandler {
        public boolean quit() {
            if (controller.isDirty()) {
                int result = showConfirmDialog(frame, "Save Changes?", "Closing", YES_NO_CANCEL_OPTION);
                switch (result) {
                    case YES_OPTION:
                        saveEntries();
                        return true;
                    case NO_OPTION:
                        // don't save, still close
                        return true;
                    default:
                        // don't close
                        return false;
                }
            }
            return true;
        }
    }
}
