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
package com.tomczarniecki.jpasskeep.crypto;

import com.tomczarniecki.jpasskeep.Entry;
import org.apache.commons.lang.Validate;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class JDOMParser implements EntryParser {

    public static final String ROOT = "list";
    public static final String ENTRY = "entry";
    public static final String DESCRIPTION = "description";
    public static final String CATEGORY = "category";
    public static final String NOTES = "notes";
    public static final String PASSWORD = "password";
    public static final String USERNAME = "username";

    @SuppressWarnings("unchecked")
    public List<Entry> read(byte[] ba) throws ParserException {
        try {
            List<Entry> entries = new ArrayList<Entry>();
            SAXBuilder builder = new SAXBuilder();
            Document doc = builder.build(new ByteArrayInputStream(ba));
            Element root = doc.getRootElement();
            Validate.isTrue(root.getName().equals(ROOT), "Unknown XML root: ", root.getName());
            for (Object obj : root.getChildren()) {
                Element node = (Element) obj;
                entries.add(decode(node));
            }
            Collections.sort(entries);
            return entries;

        } catch (JDOMException e) {
            throw new ParserException(e);
        } catch (IOException e) {
            throw new ParserException(e);
        }
    }

    public byte[] write(List<Entry> entries) throws ParserException {
        try {
            Element root = new Element(ROOT);
            for (Entry entry : entries) {
                root.addContent(encode(entry));
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
            XMLOutputter writer = new XMLOutputter();
            writer.output(new Document(root), baos);
            return baos.toByteArray();

        } catch (IOException e) {
            throw new ParserException(e);
        }
    }

    private Entry decode(Element node) {
        Validate.isTrue(ENTRY.equals(node.getName()), "Not a valid element: ", node.getName());
        Entry entry = new Entry();
        entry.setDescription(node.getChildText(DESCRIPTION));
        entry.setCategoryName(node.getChildText(CATEGORY));
        entry.setUsername(node.getChildText(USERNAME));
        entry.setPassword(node.getChildText(PASSWORD));
        entry.setNotes(node.getChildText(NOTES));
        return entry;
    }

    private Element encode(Entry entry) {
        Element node = new Element(ENTRY);
        addNode(node, DESCRIPTION, entry.getDescription());
        addNode(node, CATEGORY, entry.getCategoryName());
        addNode(node, USERNAME, entry.getUsername());
        addNode(node, PASSWORD, entry.getPassword());
        addNode(node, NOTES, entry.getNotes());
        return node;
    }

    private void addNode(Element entry, String name, String value) {
        Element node = new Element(name);
        node.setText(value);
        entry.addContent(node);
    }
}
