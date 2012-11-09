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
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import java.io.ByteArrayOutputStream;
import java.security.Provider;

public class JCEGibberishAESCrypt implements GibberishAESCrypt {

    private static final String CIPHER_ALG = "PBEWITHMD5AND256BITAES-CBC-OPENSSL";
    private static final Provider CIPHER_PROVIDER = new BouncyCastleProvider();

    public String encrypt(String plainText, char[] password) throws Exception {
        byte[] salt = RandomUtils.nextBytes(8);

        Cipher cipher = createCipher(Cipher.ENCRYPT_MODE, salt, password);
        byte[] cipherText = cipher.doFinal(plainText.getBytes(UTF_8));

        ByteArrayOutputStream baos = new ByteArrayOutputStream(cipherText.length + 16);
        baos.write(PREFIX.getBytes(UTF_8));
        baos.write(salt);
        baos.write(cipherText);

        return Codec.encodeToBase64(baos.toByteArray());
    }

    public String decrypt(String cipherText, char[] password) throws Exception {
        byte[] input = Codec.decodeFromBase64(cipherText);

        String prefixText = new String(input, 0, 8, UTF_8);
        Validate.isTrue(prefixText.equals(PREFIX), "Invalid prefix: ", prefixText);

        byte[] salt = new byte[8];
        System.arraycopy(input, 8, salt, 0, salt.length);

        Cipher cipher = createCipher(Cipher.DECRYPT_MODE, salt, password);
        byte[] plainText = cipher.doFinal(input, 16, input.length - 16);

        return new String(plainText, UTF_8);
    }

    private Cipher createCipher(int cipherMode, byte[] salt, char[] password) throws Exception {
        PBEKeySpec pbeSpec = new PBEKeySpec(password);
        SecretKeyFactory keyFact = SecretKeyFactory.getInstance(CIPHER_ALG, CIPHER_PROVIDER);
        PBEParameterSpec defParams = new PBEParameterSpec(salt, 0);

        Cipher cipher = Cipher.getInstance(CIPHER_ALG, CIPHER_PROVIDER);
        cipher.init(cipherMode, keyFact.generateSecret(pbeSpec), defParams);
        return cipher;
    }
}
