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

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.print.Book;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.util.ArrayList;
import java.util.List;

public class Printer implements Runnable, Printable {

    private final ErrorDisplay display;
    private final List<String> text;

    private List<Object> pages;
    private Font font;
    private PageFormat pf;

    public Printer(ErrorDisplay display, List<String> text) {
        this.display = display;
        this.text = text;

        this.pages = new ArrayList<>();
        this.font = new Font("Monospaced", Font.PLAIN, 8);
    }

    public void run() {
        try {
            PrinterJob job = PrinterJob.getPrinterJob();
            pf = job.defaultPage();

            Book book = new Book();
            formatPages(pf);
            book.append(this, pf, pages.size());
            job.setPageable(book);

            if (job.printDialog()) {
                job.print();
            }
        } catch (Exception e) {
            e.printStackTrace();
            display.displayError("Print Error", e.toString());
        }
    }

    private void formatPages(PageFormat pf) {
        pages.clear();
        final int maxh = (int) pf.getImageableHeight();
        final int lineh = font.getSize();
        int pageh = 0;
        List<Object> page = new ArrayList<>();
        for (String item : text) {
            if (pageh + lineh > maxh) {
                pages.add(page);
                page = new ArrayList<>();
                pageh = 0;
            }
            page.add(item);
            pageh += lineh;
        }
        pages.add(page);
    }

    public int print(Graphics graphics, PageFormat pf, int pageIndex) throws PrinterException {
        if (this.pf != pf) {
            this.pf = pf;
            formatPages(pf);
        }
        if (pageIndex >= pages.size()) {
            return Printable.NO_SUCH_PAGE;
        }
        graphics.setFont(font);
        graphics.setColor(Color.BLACK);
        renderPage(graphics, pf, pageIndex);
        return Printable.PAGE_EXISTS;
    }

    @SuppressWarnings({"rawtypes"})
    private void renderPage(Graphics graphics, PageFormat pf, int pageIndex) {
        final int xo = (int) pf.getImageableX();
        final int yo = (int) pf.getImageableY();
        final int lineh = font.getSize();
        int y = lineh;
        List page = (List) pages.get(pageIndex);
        for (Object obj : page) {
            String text = (String) obj;
            graphics.drawString(text, xo, y + yo);
            y += lineh;
        }
    }
}
