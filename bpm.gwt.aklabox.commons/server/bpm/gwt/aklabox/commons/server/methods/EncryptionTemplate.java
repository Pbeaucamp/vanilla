package bpm.gwt.aklabox.commons.server.methods;


//TODO: REFACTOR ENCRYPTION - Refactor the system someday
public class EncryptionTemplate {
//	public void encrypt(Documents doc, CommonSession session) throws Exception {
//		if (doc.isEncrypt()) {
//			InputStream file = new FileInputStream(doc.getFilePath());
//			ByteArrayOutputStream bit = FileEncryptor.encrypt(doc.getEncryptPassword(), file);
//			OutputStream os = new FileOutputStream(doc.getFilePath());
//			bit.writeTo(os);
//		}
//
//		InputStream is = new FileInputStream(doc.getFilePath());
//
//		if (Boolean.parseBoolean(ConfigurationManager.getInstance().getVanillaConfiguration().getProperty("bpm.hdfs.use"))) {
//			HdfsHelper.addDocument(is, doc.getFilePath(), doc.getFileExtension());
//
//			if (!doc.getType().equals(DocumentUtils.IMAGE)) {
//				File file2 = new File(doc.getFilePath());
//				if (file2.delete()) {
//					System.out.println(file2.getName() + " is deleted!");
//				}
//				else {
//					System.out.println("Delete operation is failed.");
//				}
//			}
//			decrypt(doc, session);
//		}
//		else {
//			if (doc.isEncrypt()) {
//				byte[] buffer = new byte[1024];
//				FileOutputStream fos = new FileOutputStream(doc.getFilePath().replace("." + FilenameUtils.getExtension(doc.getFilePath()), "") + "2" + "." + FilenameUtils.getExtension(doc.getFilePath()));
//				int len;
//				while ((len = is.read(buffer)) > 0) {
//					fos.write(buffer, 0, len);
//				}
//				fos.close();
//			}
//			decrypt(doc, session);
//		}
//	}
//
//	public void decrypt(Documents doc, CommonSession session) throws Exception {
//		InputStream is = null;
//
//		if (Boolean.parseBoolean(ConfigurationManager.getInstance().getVanillaConfiguration().getProperty("bpm.hdfs.use"))) {
//			is = HdfsHelper.loadFileFromHDFS(File.separator + doc.getFilePath());
//
//			if (!doc.getType().equals(DocumentUtils.IMAGE)) {
//				byte[] buffer = new byte[1024];
//				FileOutputStream fos = new FileOutputStream(doc.getFilePath());
//				int len;
//				while ((len = is.read(buffer)) > 0) {
//					fos.write(buffer, 0, len);
//				}
//				fos.close();
//			}
//		}
//		else {
//			if (doc.isEncrypt()) {
//				is = new FileInputStream(doc.getFilePath().replace("." + FilenameUtils.getExtension(doc.getFilePath()), "") + "2" + "." + FilenameUtils.getExtension(doc.getFilePath()));
//
//				OutputStream out = new FileOutputStream(new File(doc.getFilePath()));
//
//				byte[] buf = new byte[1024];
//				int len;
//				while ((len = is.read(buf)) > 0) {
//					out.write(buf, 0, len);
//				}
//				is.close();
//				out.close();
//			}
//		}
//
//		if (doc.isEncrypt()) {
//			InputStream file = new FileInputStream(doc.getFilePath());
//			ByteArrayOutputStream bit = FileEncryptor.decrypt(doc.getEncryptPassword(), file);
//			OutputStream os = new FileOutputStream(doc.getFilePath());
//			bit.writeTo(os);
//			System.gc();
//		}
//	}
//
//	public void decryptForVersion(Versions version, CommonSession session) throws Exception {
//		Documents doc = session.getAklaboxService().getDocInfo(version.getDocId());
//		InputStream is = null;
//
//		if (Boolean.parseBoolean(ConfigurationManager.getInstance().getVanillaConfiguration().getProperty("bpm.hdfs.use"))) {
//			is = HdfsHelper.loadFileFromHDFS(File.separator + version.getDocRelPath());
//			if (!doc.getType().equals(DocumentUtils.IMAGE)) {
//				byte[] buffer = new byte[1024];
//				FileOutputStream fos = new FileOutputStream(version.getDocRelPath());
//				int len;
//				while ((len = is.read(buffer)) > 0) {
//					fos.write(buffer, 0, len);
//				}
//				fos.close();
//			}
//		}
//		else {
//			if (doc.isEncrypt()) {
//				is = new FileInputStream(version.getDocRelPath().replace("." + FilenameUtils.getExtension(version.getDocRelPath()), "") + "2" + "." + FilenameUtils.getExtension(version.getDocRelPath()));
//				OutputStream out = new FileOutputStream(new File(version.getDocRelPath()));
//
//				byte[] buf = new byte[1024];
//				int len;
//				while ((len = is.read(buf)) > 0) {
//					out.write(buf, 0, len);
//				}
//				is.close();
//				out.close();
//
//				System.gc();
//			}
//		}
//
//		if (doc.isEncrypt()) {
//			InputStream file = new FileInputStream(version.getDocRelPath());
//			ByteArrayOutputStream bit = FileEncryptor.decrypt(doc.getEncryptPassword(), file);
//			OutputStream os = new FileOutputStream(version.getDocRelPath());
//			bit.writeTo(os);
//		}
//	}
//
//	public void encryptForVersion(Versions version, CommonSession session) throws Exception {
//		Documents doc = session.getAklaboxService().getDocInfo(version.getDocId());
//		if (doc.isEncrypt()) {
//			InputStream file = new FileInputStream(version.getDocRelPath());
//			ByteArrayOutputStream bit = FileEncryptor.encrypt(doc.getEncryptPassword(), file);
//			OutputStream os = new FileOutputStream(version.getDocRelPath());
//			bit.writeTo(os);
//		}
//
//		InputStream is = new FileInputStream(version.getDocRelPath());
//
//		if (Boolean.parseBoolean(ConfigurationManager.getInstance().getVanillaConfiguration().getProperty("bpm.hdfs.use"))) {
//			HdfsHelper.addDocument(is, version.getDocRelPath(), doc.getFileExtension());
//
//			if (!doc.getType().equals(DocumentUtils.IMAGE)) {
//				File file2 = new File(version.getDocRelPath());
//				if (file2.delete()) {
//					System.out.println(file2.getName() + " is deleted!");
//				}
//				else {
//					System.out.println("Delete operation is failed.");
//				}
//			}
//			decrypt(doc, session);
//		}
//		else {
//			if (doc.isEncrypt()) {
//				byte[] buffer = new byte[1024];
//				FileOutputStream fos = new FileOutputStream(version.getDocRelPath().replace("." + FilenameUtils.getExtension(doc.getFilePath()), "") + "2" + "." + FilenameUtils.getExtension(version.getDocRelPath()));
//				int len;
//				while ((len = is.read(buffer)) > 0) {
//					fos.write(buffer, 0, len);
//				}
//				fos.close();
//			}
//			decrypt(doc, session);
//		}
//	}
}