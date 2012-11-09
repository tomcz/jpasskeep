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

import com.tomczarniecki.jpasskeep.RandomUtils;
import org.apache.commons.lang.Validate;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.PBEParametersGenerator;
import org.bouncycastle.crypto.engines.AESFastEngine;
import org.bouncycastle.crypto.generators.OpenSSLPBEParametersGenerator;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class LightGibberishAESCrypt implements GibberishAESCrypt {

    public String encrypt(String plainText, char[] password) throws Exception {
        byte[] salt = RandomUtils.nextBytes(8);

        ByteArrayInputStream input = new ByteArrayInputStream(plainText.getBytes(UTF_8));
        byte[] cipherText = process(true, input, password, salt);

        ByteArrayOutputStream baos = new ByteArrayOutputStream(cipherText.length + 16);
        baos.write(PREFIX.getBytes(UTF_8));
        baos.write(salt);
        baos.write(cipherText);

        return Codec.encodeToBase64(baos.toByteArray());
    }

    public String decrypt(String cipherText, char[] password) throws Exception {
        byte[] cipherTextBytes = Codec.decodeFromBase64(cipherText);

        String prefixText = new String(cipherTextBytes, 0, 8, UTF_8);
        Validate.isTrue(prefixText.equals(PREFIX), "Invalid prefix: ", prefixText);

        byte[] salt = new byte[8];
        System.arraycopy(cipherTextBytes, 8, salt, 0, salt.length);

        ByteArrayInputStream input = new ByteArrayInputStream(cipherTextBytes, 16, cipherTextBytes.length - 16);
        byte[] plainText = process(false, input, password, salt);

        return new String(plainText, UTF_8);
    }

    private byte[] process(boolean forEncryption, ByteArrayInputStream input, char[] password, byte[] salt)
            throws Exception {

        OpenSSLPBEParametersGenerator generator = new OpenSSLPBEParametersGenerator();
        generator.init(PBEParametersGenerator.PKCS5PasswordToUTF8Bytes(password), salt);
        CipherParameters params = generator.generateDerivedParameters(256, 128);

        BufferedBlockCipher cipher = new PaddedBufferedBlockCipher(new CBCBlockCipher(new AESFastEngine()));
        cipher.init(forEncryption, params);

        return LightCryptUtils.processInput(cipher, input);
    }
}
