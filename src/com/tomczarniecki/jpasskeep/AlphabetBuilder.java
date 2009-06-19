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

/**
 * ???
 *
 * @author Tom Czarniecki
 */
public class AlphabetBuilder {

    static final String SPECIAL_CHARS = "~!@#$%^&*_+-=:;<>,.?";

    static final String UPPER_CASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    static final String LOWER_CASE = "abcdefhgijklmnopqrstuvwxyz";
    static final String DIGITS = "0123456789";

    static final String NON_CONF_UPPER = "ABCDEFGHJKMNPQRSTWXYZ";
    static final String NON_CONF_LOWER = "abcdefghjkmnpqrstwxyz";
    static final String NON_CONF_DIGITS = "23456789";

    private boolean upperCase;
    private boolean lowerCase;
    private boolean digits;
    private boolean specialChars;
    private boolean nonConfusing;

    public void setDigits(boolean digits) {
        this.digits = digits;
    }

    public void setLowerCase(boolean lowerCase) {
        this.lowerCase = lowerCase;
    }

    public void setNonConfusing(boolean nonConfusing) {
        this.nonConfusing = nonConfusing;
    }

    public void setSpecialChars(boolean specialChars) {
        this.specialChars = specialChars;
    }

    public void setUpperCase(boolean upperCase) {
        this.upperCase = upperCase;
    }

    public String buildAlphabet() {
        StringBuilder alphabet = new StringBuilder();
        if (upperCase) {
            alphabet.append(nonConfusing ? NON_CONF_UPPER : UPPER_CASE);
        }
        if (lowerCase) {
            alphabet.append(nonConfusing ? NON_CONF_LOWER : LOWER_CASE);
        }
        if (digits) {
            alphabet.append(nonConfusing ? NON_CONF_DIGITS : DIGITS);
        }
        if (specialChars) {
            alphabet.append(SPECIAL_CHARS);
        }
        return alphabet.toString();
    }
}
