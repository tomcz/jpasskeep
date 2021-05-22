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
import com.tomczarniecki.jpasskeep.RandomUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.StringTokenizer;

public class EntryCipher {

    static final int CIPHER_SALT_SIZE = 20;
    static final int CIPHER_ITERATIONS = 1024;
    static final String CHARSET = "US-ASCII";
    static final String CRLF = "\r\n";
    static final int VERSION = 1;

    private final PBECrypt cipher = new LightPBECrypt();
    private final EntryParser parser = new JDOMParser();

    public List<Entry> decrypt(File file, char[] password) throws CryptoException, ParserException {
        List<String> lines = readlines(file);
        StringTokenizer tok = new StringTokenizer(lines.remove(0), "|");
        if (tok.countTokens() != 3) {
            throw new ParserException("Invalid file - bad token count: " + tok.countTokens());
        }
        int version = Integer.parseInt(tok.nextToken());
        if (version != VERSION) {
            throw new ParserException("Invalid file - bad version: " + version);
        }

        byte[] salt = Codec.decodeFromBase64(tok.nextToken());
        int iterationCount = Integer.parseInt(tok.nextToken());

        byte[] cipherText = Codec.decodeFromBase64(StringUtils.join(lines.iterator(), ""));
        byte[] plainText = cipher.decrypt(cipherText, password, salt, iterationCount);
        return parser.read(plainText);
    }

    public void encrypt(List<Entry> list, File file, char[] password) throws CryptoException, ParserException {
        byte[] buf = parser.write(list);
        int iterationCount = CIPHER_ITERATIONS + (RandomUtils.nextInt() & 0x3ff);
        byte[] salt = RandomUtils.nextBytes(CIPHER_SALT_SIZE);
        String header = String.format("%d|%s|%d", VERSION, Codec.encodeToBase64(salt), iterationCount);
        String body = Codec.encodeToBase64Chunked(cipher.encrypt(buf, password, salt, iterationCount));
        writeTextToFile(file, header + CRLF + body);
    }

    private List<String> readlines(File file) throws ParserException {
        try {
            return FileUtils.readLines(file, CHARSET);
        } catch (IOException e) {
            throw new ParserException(e);
        }
    }

    private void writeTextToFile(File file, String text) throws ParserException {
        try {
            FileUtils.writeStringToFile(file, text, CHARSET);
        } catch (IOException e) {
            throw new ParserException(e);
        }
    }
}
