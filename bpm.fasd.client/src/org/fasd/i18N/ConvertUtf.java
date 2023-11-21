package org.fasd.i18N;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

public class ConvertUtf {

	/**
	 * @param p_strToDec
	 *            the string encoding in unicode to convert to Utf-8
	 * @return the right encoded string
	 */
	public static String decodeCyrillic(String p_strToDec) {

		String roundTrip = ""; //$NON-NLS-1$
		try {
			String str = new String(p_strToDec);
			roundTrip = new String(str.getBytes("UTF-8"), "UTF-8"); //$NON-NLS-1$ //$NON-NLS-2$

		} catch (UnsupportedEncodingException e) {
		}
		return roundTrip;
	}// decode

	/**
	 * @param p_strToEnc
	 *            the string encoding in Utf-8 to convert to unicode
	 * @return the right encoded string
	 */
	public static String encodeCyrillic(String p_strToEnc) {
		String test = ""; //$NON-NLS-1$
		try {
			byte[] utf8Bytes = p_strToEnc.getBytes("UTF-8"); //$NON-NLS-1$
			test = new String(utf8Bytes, "KOI8-R"); //$NON-NLS-1$
		} catch (UnsupportedEncodingException e) {
		}
		return test;

	}// encode

	public static String testEncode(byte[] bytes) {
		String res = ""; //$NON-NLS-1$
		ByteBuffer bb = ByteBuffer.wrap(bytes);
		System.out.println("Initial Byte Buffer"); //$NON-NLS-1$
		print(bb);
		Charset[] csets = { Charset.forName("UTF-16LE"), Charset.forName("UTF-16BE"), Charset.forName("UTF-8"), Charset.forName("US-ASCII"), Charset.forName("ISO-8859-1"), Charset.forName("KOI8-R"), Charset.forName("ISO-8859-5") }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
		for (int i = 0; i < csets.length; i++) {
			System.out.println(csets[i].name() + ":"); //$NON-NLS-1$
			print(csets[i].encode(bb.asCharBuffer()));
			csets[i].decode(bb);
			bb.rewind();
		}

		return res;
	}

	public static void print(ByteBuffer bb) {
		while (bb.hasRemaining())
			System.out.print(bb.get() + " "); //$NON-NLS-1$
		System.out.println();
		bb.rewind();
	}

}// class