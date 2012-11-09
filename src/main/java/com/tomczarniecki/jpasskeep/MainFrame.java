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

import com.tomczarniecki.jpasskeep.crypto.EntryCipher;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSplitPane;
import java.awt.event.ActionListener;
import java.beans.EventHandler;

public class MainFrame extends JFrame {

    private final Display display;
    private final EntryCipher cipher;

    public MainFrame(MainListController controller, EntryCipher cipher, Worker worker, PreferenceSetter prefs) {
        super("Password Keeper");

        this.display = new Display(this, worker);
        this.cipher = cipher;

        MainDetailsArea details = new MainDetailsArea(controller);

        prefs.setDetailsPrefs(details);
        prefs.setFilterPrefs(controller);

        JSplitPane display = createDisplay(controller, details);
        setJMenuBar(createMenuBar(controller, details));
        getContentPane().add(display);

        prefs.setFramePrefs(this);
        prefs.setDisplayPrefs(display);
    }

    public Display getDisplay() {
        return display;
    }

    private JSplitPane createDisplay(MainListController controller, MainDetailsArea details) {
        JSplitPane splitDisplay = new JSplitPane();
        splitDisplay.setOrientation(JSplitPane.VERTICAL_SPLIT);
        splitDisplay.setTopComponent(controller.getDisplay());
        splitDisplay.setBottomComponent(details.getDisplay());
        splitDisplay.setOneTouchExpandable(true);
        return splitDisplay;
    }

    private JMenuBar createMenuBar(MainListController controller, MainDetailsArea details) {
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(createListMenu(controller));
        menuBar.add(createViewMenu(controller, details));
        menuBar.add(createEntryMenu(controller));
        menuBar.add(createCopyMenu(controller));
        return menuBar;
    }

    private JMenu createListMenu(MainListController controller) {
        JMenu listMenu = new JMenu("List");
        listMenu.add(new JMenuItem(new ImportListAction(controller, display, cipher)));
        listMenu.add(new JMenuItem(new ExportListAction(controller, display, cipher)));
        listMenu.add(new JMenuItem(new PrintListAction(controller, display)));
        listMenu.add(new JMenuItem(new HtmlExportAction(controller, display)));
        return listMenu;
    }

    private JMenu createEntryMenu(MainListController controller) {
        JMenu entryMenu = new JMenu("Entry");
        entryMenu.add(new JMenuItem(new NewEntryAction(controller, new EntryDialog(this, "New Entry"))));
        entryMenu.add(new JMenuItem(new EditEntryAction(controller, new EntryDialog(this, "Edit Entry"))));
        entryMenu.add(new JMenuItem(new DeleteEntryAction(controller, display, "Delete Entry")));
        return entryMenu;
    }

    private JMenu createCopyMenu(MainListController controller) {
        JMenu copyMenu = new JMenu("Copy");
        copyMenu.add(new JMenuItem(new CopyUsernameAction(controller, display)));
        copyMenu.add(new JMenuItem(new CopyPasswordAction(controller, display)));
        return copyMenu;
    }

    private JMenu createViewMenu(MainListController controller, MainDetailsArea details) {
        JMenu viewMenu = new JMenu("View");
        viewMenu.add(createToggleItem("Show Home Entries", controller, "showHome", controller.isShowHome()));
        viewMenu.add(createToggleItem("Show Work Entries", controller, "showWork", controller.isShowWork()));
        viewMenu.add(createToggleItem("Show Other Entries", controller, "showOther", controller.isShowOther()));
        viewMenu.addSeparator();
        viewMenu.add(createToggleItem("Show Passwords", details, "showPasswd", details.isShowPasswd()));
        viewMenu.add(createToggleItem("Show Notes", details, "showNotes", details.isShowNotes()));
        viewMenu.addSeparator();
        viewMenu.add(createInvokeItem("Password Generator", new PasswordBuilderDialog(this), "display"));
        return viewMenu;
    }

    private JCheckBoxMenuItem createToggleItem(String label, Object target, String action, boolean selected) {
        JCheckBoxMenuItem item = new JCheckBoxMenuItem(label, selected);
        item.addActionListener(EventHandler.create(ActionListener.class, target, action, "source.selected"));
        return item;
    }

    private JMenuItem createInvokeItem(String label, Object target, String action) {
        JMenuItem item = new JMenuItem(label);
        item.addActionListener(EventHandler.create(ActionListener.class, target, action));
        return item;
    }
}
