package bpm.vanilla.platform.core.utils;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class Test {

	private static final String PASSWORD = "t[k&P;8#.dmR";
	private static final String PUB_KEY_FILE = "D:/DATA/Test/Encryption/public1.key";
	private static final String PRIV_KEY_FILE = "D:/DATA/Test/Encryption/private1.key";

	private static final String E_MESSAGE = "D:/DATA/Test/Encryption/message.txt";
	private static final String E_OUTPUT_NAME = "D:/DATA/Test/Encryption/message_encrypt.txt";

	private static final String DE_MESSAGE = "D:/DATA/Test/Encryption/message_encrypt.txt";
	private static final String DE_OUTPUT_NAME = "D:/DATA/Test/Encryption/message_decrypt.txt";

	private static final boolean withFile = false;

	public static void main(String[] args) throws Exception {
		if (withFile) {
			encryptWithFile();
			decryptWithFile();
		}
		else {
			encryptWithPassword(PASSWORD);
			decryptWithPassword(PASSWORD);
		}
	}

	private static void encryptWithFile() throws Exception {
		FileInputStream is = new FileInputStream(E_MESSAGE);

		try {
			ByteArrayOutputStream bos = PGPFileProcessor.encrypt(PUB_KEY_FILE, is, false, true);

			OutputStream outputStream = new FileOutputStream(E_OUTPUT_NAME);
			bos.writeTo(outputStream);
			bos.close();
			outputStream.close();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	private static void encryptWithPassword(String password) throws Exception {
		FileInputStream is = new FileInputStream(E_MESSAGE);

		try {
			ByteArrayOutputStream bos = PGPFileProcessor.encrypt(is, password, false, true);

			OutputStream outputStream = new FileOutputStream(E_OUTPUT_NAME);
			bos.writeTo(outputStream);
			bos.close();
			outputStream.close();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	private static void decryptWithFile() throws Exception {
		FileInputStream is = new FileInputStream(DE_MESSAGE);

		try {
			ByteArrayOutputStream bos = PGPFileProcessor.decrypt(PRIV_KEY_FILE, is, PASSWORD);

			OutputStream outputStream = new FileOutputStream(DE_OUTPUT_NAME);
			bos.writeTo(outputStream);
			bos.close();
			outputStream.close();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	private static void decryptWithPassword(String password) throws Exception {
		FileInputStream is = new FileInputStream(DE_MESSAGE);

		try {
			ByteArrayOutputStream bos = PGPFileProcessor.decrypt(is, PASSWORD);

			OutputStream outputStream = new FileOutputStream(DE_OUTPUT_NAME);
			bos.writeTo(outputStream);
			bos.close();
			outputStream.close();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
}
