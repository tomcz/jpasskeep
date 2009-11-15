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

import java.util.ArrayList;
import java.util.List;

public class PasswordBuilder {

    static final int MINIMUM_LENGTH = 2;

    private String alphabet;
    private boolean permitRepeats;
    private int length;

    public void setAlphabet(String alphabet) {
        this.alphabet = alphabet;
    }

    public void setPermitRepeats(boolean permitRepeats) {
        this.permitRepeats = permitRepeats;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String buildPassword() throws PasswordBuilderException {
        if (length < MINIMUM_LENGTH) {
            throw new PasswordBuilderException("Length (" + length + ") must be greater than minimum ("
                    + MINIMUM_LENGTH + ")");
        }
        if (!permitRepeats && (length > alphabet.length())) {
            throw new PasswordBuilderException("Non-repeating length (" + length
                    + ") must be less than alphabet length (" + alphabet.length() + ")");
        }
        List<Character> characters = alphabetAsList();
        StringBuilder result = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            result.append(nextCharacter(characters));
        }
        return result.toString();
    }

    private List<Character> alphabetAsList() {
        List<Character> characters = new ArrayList<Character>(alphabet.length());
        for (int i = 0; i < alphabet.length(); i++) {
            characters.add(alphabet.charAt(i));
        }
        return characters;
    }

    private Character nextCharacter(List<Character> characters) {
        int index = RandomUtils.nextInt(characters.size());
        return permitRepeats ? characters.get(index) : characters.remove(index);
    }
}
