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

import org.bouncycastle.crypto.BufferedBlockCipher;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class LightCryptUtils {

    public static byte[] processInput(BufferedBlockCipher cipher, InputStream input) throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        byte[] inputBuffer = new byte[1024];
        int inputLen = 0;

        byte[] outputBuffer = new byte[cipher.getOutputSize(inputBuffer.length)];
        int outputLen = 0;

        while ((inputLen = input.read(inputBuffer)) > -1) {
            outputLen = cipher.processBytes(inputBuffer, 0, inputLen, outputBuffer, 0);
            if (outputLen > 0) {
                output.write(outputBuffer, 0, outputLen);
            }
        }

        outputLen = cipher.doFinal(outputBuffer, 0);
        if (outputLen > 0) {
            output.write(outputBuffer, 0, outputLen);
        }

        return output.toByteArray();
    }
}
