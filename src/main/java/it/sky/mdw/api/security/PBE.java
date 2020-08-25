package it.sky.mdw.api.security;

import java.security.SecureRandom;
import java.security.Security;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Hex;

/**
 * password-based encryption
 *
 */
public final class PBE {

	private final String salt = "A long, but constant phrase that will be used each time as the salt.";
	private final int iterations = 2000;
	private final int keyLength = 256;
	private final SecureRandom random = new SecureRandom();
	private String passphrase = "The quick brown fox jumped over the lazy brown dog";
	private static PBE instance;

	private PBE() {
		Security.insertProviderAt( new BouncyCastleProvider(), 1 );
	}

	public static PBE getInstance() {
		if(instance == null)
			instance = new PBE();
		return instance;
	}

	public byte [] encrypt(byte [] plaintext) throws Exception {
		SecretKey key = generateKey(passphrase);

		Cipher cipher = Cipher.getInstance("AES/CTR/NOPADDING");
		cipher.init(Cipher.ENCRYPT_MODE, key, random);
		return Hex.encode(cipher.doFinal(plaintext));
	}

	public byte [] decrypt(byte [] ciphertext) throws Exception {
		SecretKey key = generateKey(passphrase);

		Cipher cipher = Cipher.getInstance("AES/CTR/NOPADDING");
		cipher.init(Cipher.DECRYPT_MODE, key, random);
		return cipher.doFinal(Hex.decode(ciphertext));
	}

	private SecretKey generateKey(String passphrase) throws Exception {
		PBEKeySpec keySpec = new PBEKeySpec(passphrase.toCharArray(), salt.getBytes(), iterations, keyLength);
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWITHSHA256AND256BITAES-CBC-BC");
		return keyFactory.generateSecret(keySpec);
	}

}
