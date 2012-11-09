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

import javax.swing.JFrame;
import javax.swing.JSplitPane;
import java.awt.Point;
import java.util.prefs.Preferences;

public class PreferenceSetter implements QuitHandler {

    static final String PREF_WIDTH = "width";
    static final String PREF_HEIGHT = "height";
    static final String PREF_LOCATION_X = "locationX";
    static final String PREF_LOCATION_Y = "locationY";
    static final String PREF_DIVIDER = "divider";
    static final String PREF_SHOW_NOTES = "showNotes";
    static final String PREF_SHOW_PASSWD = "showPasswd";
    static final String PREF_SHOW_HOME = "showHome";
    static final String PREF_SHOW_WORK = "showWork";
    static final String PREF_SHOW_OTHER = "showOther";

    private Preferences prefs;
    private JFrame frame;
    private JSplitPane display;
    private MainDetailsArea details;
    private MainListController controller;

    public PreferenceSetter(Class clazz) {
        this.prefs = Preferences.userNodeForPackage(clazz);
    }

    public void setFramePrefs(JFrame frame) {
        this.frame = frame;

        int width = prefs.getInt(PREF_WIDTH, 600);
        int height = prefs.getInt(PREF_HEIGHT, 500);
        frame.setSize(width, height);

        int locX = prefs.getInt(PREF_LOCATION_X, 0);
        int locY = prefs.getInt(PREF_LOCATION_Y, 0);
        if ((locX == 0) && (locY == 0)) {
            frame.setLocationRelativeTo(null);
        } else {
            frame.setLocation(locX, locY);
        }
    }

    public void setDisplayPrefs(JSplitPane display) {
        this.display = display;
        int dividerLoc = prefs.getInt(PREF_DIVIDER, 250);
        display.setDividerLocation(dividerLoc);
    }

    public void setDetailsPrefs(MainDetailsArea details) {
        this.details = details;
        details.setShowNotes(prefs.getBoolean(PREF_SHOW_NOTES, true));
        details.setShowPasswd(prefs.getBoolean(PREF_SHOW_PASSWD, true));
    }

    public void setFilterPrefs(MainListController controller) {
        this.controller = controller;
        controller.setShowHome(prefs.getBoolean(PREF_SHOW_HOME, true));
        controller.setShowWork(prefs.getBoolean(PREF_SHOW_WORK, true));
        controller.setShowOther(prefs.getBoolean(PREF_SHOW_OTHER, true));
    }

    public boolean quit() {
        Point frameLocation = frame.getLocation();
        prefs.putInt(PREF_WIDTH, frame.getWidth());
        prefs.putInt(PREF_HEIGHT, frame.getHeight());
        prefs.putInt(PREF_LOCATION_X, (int) frameLocation.getX());
        prefs.putInt(PREF_LOCATION_Y, (int) frameLocation.getY());
        prefs.putInt(PREF_DIVIDER, display.getDividerLocation());
        prefs.putBoolean(PREF_SHOW_NOTES, details.isShowNotes());
        prefs.putBoolean(PREF_SHOW_PASSWD, details.isShowPasswd());
        prefs.putBoolean(PREF_SHOW_HOME, controller.isShowHome());
        prefs.putBoolean(PREF_SHOW_WORK, controller.isShowWork());
        prefs.putBoolean(PREF_SHOW_OTHER, controller.isShowOther());
        return true;
    }
}
