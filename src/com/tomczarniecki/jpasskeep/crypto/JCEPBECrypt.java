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

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import java.security.Provider;

/**
 * ???
 *
 * @author Tom Czarniecki
 */
public class JCEPBECrypt implements PBECrypt {

    private static final Provider CIPHER_PROVIDER = new BouncyCastleProvider();
    private static final String CIPHER_ALG = "PBEWithSHAAndTwofish-CBC";

    public byte[] encrypt(byte[] plainText, char[] passphrase, byte[] salt, int iterations) throws CryptoException {
        return process(Cipher.ENCRYPT_MODE, plainText, passphrase, salt, iterations);
    }

    public byte[] decrypt(byte[] cipherText, char[] passphrase, byte[] salt, int iterations) throws CryptoException {
        return process(Cipher.DECRYPT_MODE, cipherText, passphrase, salt, iterations);
    }

    private byte[] process(int mode, byte[] inputText, char[] passphrase, byte[] salt, int iterations)
            throws CryptoException {

        try {
            PBEKeySpec pbeSpec = new PBEKeySpec(passphrase);
            SecretKeyFactory keyFact = SecretKeyFactory.getInstance(CIPHER_ALG, CIPHER_PROVIDER);
            PBEParameterSpec defParams = new PBEParameterSpec(salt, iterations);

            Cipher cipher = Cipher.getInstance(CIPHER_ALG, CIPHER_PROVIDER);
            cipher.init(mode, keyFact.generateSecret(pbeSpec), defParams);

            return cipher.doFinal(inputText);

        } catch (Exception e) {
            throw new CryptoException(e);
        }
    }
}
