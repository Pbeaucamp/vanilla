package bpm.vanilla.platform.core.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class PGPFileProcessor {

	public static ByteArrayOutputStream encrypt(String publicKeyPath, InputStream fileToEncrypt, boolean asciiArmored, boolean integrityCheck) throws Throwable {
		return encrypt(new File(publicKeyPath), fileToEncrypt, asciiArmored, integrityCheck);
	}
	
	public static ByteArrayOutputStream encrypt(File publicKeyFile, InputStream fileToEncrypt, boolean asciiArmored, boolean integrityCheck) throws Throwable {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();

		FileInputStream keyIn = new FileInputStream(publicKeyFile);
		PGPUtils.encryptFile(bos, fileToEncrypt, keyIn, asciiArmored, integrityCheck);
		keyIn.close();
		
		return bos;
	}

	public static ByteArrayOutputStream encrypt(InputStream fileToEncrypt, String password, boolean asciiArmored, boolean integrityCheck) throws Throwable {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();

		PGPUtils.encryptFile(bos, fileToEncrypt, password.toCharArray(), asciiArmored, integrityCheck);

		return bos;
	}

	public static ByteArrayOutputStream decrypt(String secretKeyPath, InputStream fileToDecrypt, String password) throws Throwable {
		return decrypt(new File(secretKeyPath), fileToDecrypt, password);
	}
	
	public static ByteArrayOutputStream decrypt(File secretKeyFile, InputStream fileToDecrypt, String password) throws Throwable {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();

		FileInputStream keyIn = new FileInputStream(secretKeyFile);
		PGPUtils.decryptFile(bos, fileToDecrypt, keyIn, password.toCharArray());
		keyIn.close();
		
		return bos;
	}

	public static ByteArrayOutputStream decrypt(InputStream fileToDecrypt, String password) throws Throwable {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();

		PGPUtils.decryptFile(bos, fileToDecrypt, password.toCharArray());

		return bos;
	}

	/*
	 * Not tested
	 */
//	public static ByteArrayOutputStream signEncrypt(String publicKeyPath, String secretKeyPath, InputStream fileToEncrypt, String passphrase, boolean asciiArmored, boolean integrityCheck) throws Exception {
//		ByteArrayOutputStream bos = new ByteArrayOutputStream();
//
//		FileInputStream publicKeyIn = new FileInputStream(publicKeyPath);
//		FileInputStream secretKeyIn = new FileInputStream(secretKeyPath);
//
//		PGPPublicKey publicKey = PGPUtils.readPublicKey(publicKeyIn);
//		PGPSecretKey secretKey = PGPUtils.readSecretKey(secretKeyIn);
//
//		PGPUtils.signEncryptFile(bos, fileToEncrypt, publicKey, secretKey, passphrase, asciiArmored, integrityCheck);
//
//		publicKeyIn.close();
//		secretKeyIn.close();
//
//		return bos;
//	}
}
