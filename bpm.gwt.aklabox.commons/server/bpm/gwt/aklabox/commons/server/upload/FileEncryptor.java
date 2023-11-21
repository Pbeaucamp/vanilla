package bpm.gwt.aklabox.commons.server.upload;


public class FileEncryptor {

//	private static final String ALGO = "AES/ECB/PKCS5Padding";
//	private static final String KEY_ALGORITHM = "AES";
//
//	public static ByteArrayOutputStream encrypt(String publicKey, InputStream fileToEncrypt) throws Exception {
//		ByteArrayOutputStream bos = new ByteArrayOutputStream();
//
//		SecretKeySpec key = buildKey(publicKey);
//		// creating and initialising cipher and cipher streams
//		Cipher encrypt = Cipher.getInstance(ALGO);
//		encrypt.init(Cipher.ENCRYPT_MODE, key);
//		CipherOutputStream cout = new CipherOutputStream(bos, encrypt);
//
//		byte[] buf = new byte[1024];
//		int read;
//		while ((read = fileToEncrypt.read(buf)) != -1) {
//			cout.write(buf, 0, read); // writing encrypted data
//		}
//		fileToEncrypt.close();
//		cout.flush();
//		cout.close();
//
//		return bos;
//	}
//
//	public static ByteArrayOutputStream decrypt(String publicKey, InputStream fileToEncrypt) throws Exception {
//		ByteArrayOutputStream bos = new ByteArrayOutputStream();
//
//		SecretKeySpec key = buildKey(publicKey);
//		// creating and initialising cipher and cipher streams
//		Cipher decrypt = Cipher.getInstance(ALGO);
//		decrypt.init(Cipher.DECRYPT_MODE, key);
//		CipherInputStream cin = new CipherInputStream(fileToEncrypt, decrypt);
//
//		byte[] buf = new byte[1024];
//		int read = 0;
//		while ((read = cin.read(buf)) != -1) {
//			bos.write(buf, 0, read);
//		}
//		cin.close();
//		bos.flush();
//
//		return bos;
//	}
//
//	private static SecretKeySpec buildKey(String password) throws NoSuchAlgorithmException, UnsupportedEncodingException {
//		String md5Pass = MD5Helper.encode(password);
//
//		byte[] key = hexStringToByteArray(md5Pass);
//
//		SecretKeySpec spec = new SecretKeySpec(key, KEY_ALGORITHM);
//		return spec;
//	}
//
//	private static byte[] hexStringToByteArray(String s) {
//		int len = s.length();
//		byte[] data = new byte[len / 2];
//		for (int i = 0; i < len; i += 2) {
//			data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
//		}
//		return data;
//	}
}