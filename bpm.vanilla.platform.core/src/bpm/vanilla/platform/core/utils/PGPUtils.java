package bpm.vanilla.platform.core.utils;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Iterator;

import org.apache.commons.io.IOUtils;
import org.bouncycastle.bcpg.ArmoredOutputStream;
import org.bouncycastle.bcpg.CompressionAlgorithmTags;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.PGPCompressedData;
import org.bouncycastle.openpgp.PGPCompressedDataGenerator;
import org.bouncycastle.openpgp.PGPEncryptedData;
import org.bouncycastle.openpgp.PGPEncryptedDataGenerator;
import org.bouncycastle.openpgp.PGPEncryptedDataList;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPLiteralData;
import org.bouncycastle.openpgp.PGPLiteralDataGenerator;
import org.bouncycastle.openpgp.PGPOnePassSignatureList;
import org.bouncycastle.openpgp.PGPPBEEncryptedData;
import org.bouncycastle.openpgp.PGPPrivateKey;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPPublicKeyEncryptedData;
import org.bouncycastle.openpgp.PGPPublicKeyRing;
import org.bouncycastle.openpgp.PGPPublicKeyRingCollection;
import org.bouncycastle.openpgp.PGPSecretKey;
import org.bouncycastle.openpgp.PGPSecretKeyRingCollection;
import org.bouncycastle.openpgp.PGPUtil;
import org.bouncycastle.openpgp.jcajce.JcaPGPObjectFactory;
import org.bouncycastle.openpgp.operator.PGPKeyEncryptionMethodGenerator;
import org.bouncycastle.openpgp.operator.jcajce.JcaKeyFingerprintCalculator;
import org.bouncycastle.openpgp.operator.jcajce.JcaPGPDigestCalculatorProviderBuilder;
import org.bouncycastle.openpgp.operator.jcajce.JcePBEDataDecryptorFactoryBuilder;
import org.bouncycastle.openpgp.operator.jcajce.JcePBEKeyEncryptionMethodGenerator;
import org.bouncycastle.openpgp.operator.jcajce.JcePBESecretKeyDecryptorBuilder;
import org.bouncycastle.openpgp.operator.jcajce.JcePGPDataEncryptorBuilder;
import org.bouncycastle.openpgp.operator.jcajce.JcePublicKeyDataDecryptorFactoryBuilder;
import org.bouncycastle.openpgp.operator.jcajce.JcePublicKeyKeyEncryptionMethodGenerator;
import org.bouncycastle.util.io.Streams;

public class PGPUtils {

	public static void encryptFile(OutputStream out, InputStream fileStream, FileInputStream keyIn, boolean armor, boolean withIntegrityCheck) throws IOException, NoSuchProviderException, PGPException {
		PGPPublicKey encKey = PGPUtils.readPublicKey(keyIn);
		encryptFile(out, fileStream, new JcePublicKeyKeyEncryptionMethodGenerator(encKey).setProvider("BC"), armor, withIntegrityCheck);
	}

	public static void encryptFile(OutputStream out, InputStream fileStream, char[] passPhrase, boolean armor, boolean withIntegrityCheck) throws IOException, NoSuchProviderException, PGPException {
		encryptFile(out, fileStream, new JcePBEKeyEncryptionMethodGenerator(passPhrase).setProvider("BC"), armor, withIntegrityCheck);
	}

	private static void encryptFile(OutputStream out, InputStream fileStream, PGPKeyEncryptionMethodGenerator encryptionMethod, boolean armor, boolean withIntegrityCheck) throws IOException, NoSuchProviderException, PGPException {
		Security.addProvider(new BouncyCastleProvider());

		if (armor) {
			out = new ArmoredOutputStream(out);
		}

		byte[] compressedData = compressFile(fileStream, CompressionAlgorithmTags.ZIP);

		PGPEncryptedDataGenerator encGen = new PGPEncryptedDataGenerator(new JcePGPDataEncryptorBuilder(PGPEncryptedData.CAST5).setWithIntegrityPacket(withIntegrityCheck).setSecureRandom(new SecureRandom()).setProvider("BC"));
		encGen.addMethod(encryptionMethod);

		OutputStream encOut = encGen.open(out, compressedData.length);
		encOut.write(compressedData);
		encOut.close();

		if (armor) {
			out.close();
		}
	}
	
	public static void decryptFile(OutputStream out, InputStream fileStream, char[] passPhrase) throws IOException, NoSuchProviderException, PGPException {
		decryptFile(out, fileStream, null, passPhrase);
	}

	@SuppressWarnings("rawtypes")
	public static void decryptFile(OutputStream out, InputStream fileStream, InputStream keyIn, char[] passPhrase) throws IOException, NoSuchProviderException, PGPException {
		Security.addProvider(new BouncyCastleProvider());
		
		fileStream = PGPUtil.getDecoderStream(fileStream);

		try {
			JcaPGPObjectFactory pgpF = new JcaPGPObjectFactory(fileStream);
			PGPEncryptedDataList enc;

			Object o = pgpF.nextObject();
			//
			// the first object might be a PGP marker packet.
			//
			if (o instanceof PGPEncryptedDataList) {
				enc = (PGPEncryptedDataList) o;
			}
			else {
				enc = (PGPEncryptedDataList) pgpF.nextObject();
			}

			InputStream clear = null;
			PGPEncryptedData pbe = null;
			if (keyIn == null) {
				pbe = (PGPPBEEncryptedData) enc.get(0);
				clear = ((PGPPBEEncryptedData) pbe).getDataStream(new JcePBEDataDecryptorFactoryBuilder(new JcaPGPDigestCalculatorProviderBuilder().setProvider("BC").build()).setProvider("BC").build(passPhrase));
			}
			else {
				PGPPrivateKey sKey = null;
				PGPSecretKeyRingCollection pgpSec = new PGPSecretKeyRingCollection(PGPUtil.getDecoderStream(keyIn), new JcaKeyFingerprintCalculator());

				Iterator it = enc.getEncryptedDataObjects();
				while (sKey == null && it.hasNext()) {
					pbe = (PGPPublicKeyEncryptedData) it.next();
					sKey = findSecretKey(pgpSec, ((PGPPublicKeyEncryptedData) pbe).getKeyID(), passPhrase);
				}
				if (sKey == null) {
					throw new IllegalArgumentException("secret key for message not found.");
				}

				clear = ((PGPPublicKeyEncryptedData) pbe).getDataStream(new JcePublicKeyDataDecryptorFactoryBuilder().setProvider("BC").build(sKey));
			}
			
			JcaPGPObjectFactory plainFact = new JcaPGPObjectFactory(clear);

			Object message = plainFact.nextObject();

			if (message instanceof PGPCompressedData) {
				PGPCompressedData cData = (PGPCompressedData) message;
				JcaPGPObjectFactory pgpFact = new JcaPGPObjectFactory(cData.getDataStream());

				message = pgpFact.nextObject();
			}

			if (message instanceof PGPLiteralData) {
				PGPLiteralData ld = (PGPLiteralData) message;

				InputStream unc = ld.getInputStream();
				Streams.pipeAll(unc, out);
			}
			else if (message instanceof PGPOnePassSignatureList) {
				throw new PGPException("Encrypted message contains a signed message - not literal data.");
			}
			else {
				throw new PGPException("Message is not a simple encrypted file - type unknown.");
			}

			if (pbe.isIntegrityProtected()) {
				if (!pbe.verify()) {
					throw new PGPException("Message failed integrity check");
				}
			}
			else {
				throw new PGPException("No message integrity check");
			}
		} catch (PGPException e) {
			e.printStackTrace();
			if (e.getUnderlyingException() != null) {
				e.getUnderlyingException().printStackTrace();
			}

			throw e;
		}
	}
	
	/**
	 * A simple routine that opens a key ring file and loads the first available
	 * key suitable for encryption.
	 * 
	 * @param input
	 *            data stream containing the public key data
	 * @return the first public key found.
	 * @throws IOException
	 * @throws PGPException
	 */
	@SuppressWarnings("rawtypes")
	private static PGPPublicKey readPublicKey(InputStream input) throws IOException, PGPException {
		PGPPublicKeyRingCollection pgpPub = new PGPPublicKeyRingCollection(PGPUtil.getDecoderStream(input), new JcaKeyFingerprintCalculator());

		//
		// we just loop through the collection till we find a key suitable for
		// encryption, in the real
		// world you would probably want to be a bit smarter about this.
		//

		Iterator keyRingIter = pgpPub.getKeyRings();
		while (keyRingIter.hasNext()) {
			PGPPublicKeyRing keyRing = (PGPPublicKeyRing) keyRingIter.next();

			Iterator keyIter = keyRing.getPublicKeys();
			while (keyIter.hasNext()) {
				PGPPublicKey key = (PGPPublicKey) keyIter.next();

				if (key.isEncryptionKey()) {
					return key;
				}
			}
		}

		throw new IllegalArgumentException("Can't find encryption key in key ring.");
	}

	/**
	 * Search a secret key ring collection for a secret key corresponding to
	 * keyID if it exists.
	 * 
	 * @param pgpSec
	 *            a secret key ring collection.
	 * @param keyID
	 *            keyID we want.
	 * @param pass
	 *            passphrase to decrypt secret key with.
	 * @return the private key.
	 * @throws PGPException
	 * @throws NoSuchProviderException
	 */
	private static PGPPrivateKey findSecretKey(PGPSecretKeyRingCollection pgpSec, long keyID, char[] pass) throws PGPException, NoSuchProviderException {
		PGPSecretKey pgpSecKey = pgpSec.getSecretKey(keyID);

		if (pgpSecKey == null) {
			return null;
		}

		return pgpSecKey.extractPrivateKey(new JcePBESecretKeyDecryptorBuilder().setProvider("BC").build(pass));
	}

	private static byte[] compressFile(InputStream fileStream, int algorithm) throws IOException {
		ByteArrayOutputStream bOut = new ByteArrayOutputStream();
		PGPCompressedDataGenerator comData = new PGPCompressedDataGenerator(algorithm);

		PGPLiteralDataGenerator lData = new PGPLiteralDataGenerator();

		byte[] buffer = new byte[1 << 16];

		OutputStream pOut = lData.open(comData.open(bOut), PGPLiteralData.BINARY, PGPLiteralData.CONSOLE, PGPLiteralData.NOW, buffer);
		IOUtils.copy(fileStream, pOut);
		pOut.close();

		comData.close();
		return bOut.toByteArray();
	}

	/**
	 * A simple routine that opens a key ring file and loads the first available
	 * key suitable for signature generation.
	 * 
	 * @param input
	 *            stream to read the secret key ring collection from.
	 * @return a secret key.
	 * @throws IOException
	 *             on a problem with using the input stream.
	 * @throws PGPException
	 *             if there is an issue parsing the input stream.
	 */
	/** Not used **/
	// @SuppressWarnings("rawtypes")
	// private static PGPSecretKey readSecretKey(InputStream input) throws
	// IOException, PGPException {
	// PGPSecretKeyRingCollection pgpSec = new
	// PGPSecretKeyRingCollection(PGPUtil.getDecoderStream(input), new
	// JcaKeyFingerprintCalculator());
	//
	// //
	// // we just loop through the collection till we find a key suitable for
	// // encryption, in the real
	// // world you would probably want to be a bit smarter about this.
	// //
	//
	// Iterator keyRingIter = pgpSec.getKeyRings();
	// while (keyRingIter.hasNext()) {
	// PGPSecretKeyRing keyRing = (PGPSecretKeyRing) keyRingIter.next();
	//
	// Iterator keyIter = keyRing.getSecretKeys();
	// while (keyIter.hasNext()) {
	// PGPSecretKey key = (PGPSecretKey) keyIter.next();
	//
	// if (key.isSigningKey()) {
	// return key;
	// }
	// }
	// }
	//
	// throw new
	// IllegalArgumentException("Can't find signing key in key ring.");
	// }
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static void decryptFile(InputStream in, OutputStream out, InputStream keyIn, char[] passwd) throws IOException, NoSuchProviderException, PGPException {
		Security.addProvider(new BouncyCastleProvider());
		
		in = PGPUtil.getDecoderStream(in);

		try {
			JcaPGPObjectFactory pgpF = new JcaPGPObjectFactory(in);
			PGPEncryptedDataList enc;

			Object o = pgpF.nextObject();
			//
			// the first object might be a PGP marker packet.
			//
			if (o instanceof PGPEncryptedDataList) {
				enc = (PGPEncryptedDataList) o;
			}
			else {
				enc = (PGPEncryptedDataList) pgpF.nextObject();
			}

			//
			// find the secret key
			//
			Iterator it = enc.getEncryptedDataObjects();
			PGPPrivateKey sKey = null;
			PGPPublicKeyEncryptedData pbe = null;
			PGPSecretKeyRingCollection pgpSec = new PGPSecretKeyRingCollection(PGPUtil.getDecoderStream(keyIn), new JcaKeyFingerprintCalculator());

			while (sKey == null && it.hasNext()) {
				pbe = (PGPPublicKeyEncryptedData) it.next();
				sKey = findSecretKey(pgpSec, pbe.getKeyID(), passwd);
			}

			if (sKey == null) {
				throw new IllegalArgumentException("secret key for message not found.");
			}

			InputStream clear = pbe.getDataStream(new JcePublicKeyDataDecryptorFactoryBuilder().setProvider("BC").build(sKey));

			JcaPGPObjectFactory plainFact = new JcaPGPObjectFactory(clear);

			Object message = plainFact.nextObject();

			if (message instanceof PGPCompressedData) {
				PGPCompressedData cData = (PGPCompressedData) message;
				JcaPGPObjectFactory pgpFact = new JcaPGPObjectFactory(cData.getDataStream());

				message = pgpFact.nextObject();
			}

			if (message instanceof PGPLiteralData) {
				PGPLiteralData ld = (PGPLiteralData) message;

				InputStream unc = ld.getInputStream();
				Streams.pipeAll(unc, out);
			}
			else if (message instanceof PGPOnePassSignatureList) {
				throw new PGPException("Encrypted message contains a signed message - not literal data.");
			}
			else {
				throw new PGPException("Message is not a simple encrypted file - type unknown.");
			}

			if (pbe.isIntegrityProtected()) {
				if (!pbe.verify()) {
					throw new PGPException("Message failed integrity check");
				}
//				else {
//					throw new PGPException("Message integrity check passed");
//				}
			}
			else {
				throw new PGPException("No message integrity check");
			}
		} catch (PGPException e) {
			e.printStackTrace();
			if (e.getUnderlyingException() != null) {
				e.getUnderlyingException().printStackTrace();
			}

			throw e;
		}
	}
}