package bpm.gwt.aklabox.commons.server.scan;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;

import uk.co.mmscomputing.device.scanner.Scanner;
import uk.co.mmscomputing.device.scanner.ScannerDevice;
import uk.co.mmscomputing.device.scanner.ScannerIOMetadata;
import uk.co.mmscomputing.device.scanner.ScannerListener;
import bpm.document.management.core.model.Documents;
import bpm.document.management.core.model.Group;
import bpm.document.management.core.model.User;
import bpm.document.management.core.model.Versions;
import bpm.gwt.aklabox.commons.server.security.CommonSession;
import bpm.vanilla.platform.core.config.ConfigurationManager;

//TODO: REFACTOR RUNTIME - To move runtime side
@Deprecated
public class TwainAkLad {
	
//	private static Logger logger = Logger.getLogger(TwainAkLad.class);

	public static void scan(final CommonSession session, final Documents doc, final String aklaboxFilesPath) throws Exception {
		System.out.println("Starting scan");
		
		Scanner scanner = Scanner.getDevice();
		scanner.addListener(new ScannerListener() {

			@Override
			public void update(ScannerIOMetadata.Type type, ScannerIOMetadata metadata) {
				System.out.println("Scan state = " + type);
				if (type.equals(ScannerIOMetadata.ACQUIRED)) {
					System.out.println("Image acquired");
					BufferedImage image = metadata.getImage();
					try {
						String name = doc.getName() + System.currentTimeMillis() + "." + doc.getFileExtension();
						String fileName = aklaboxFilesPath + "Documents/Images/" + name;

						File f = new File(fileName);
						ImageIO.write(image, "png", f);

						double bytes = f.length();
						doc.setFileSize(((int) bytes) / 1000);
						doc.setThumbImage(fileName);
						doc.setName(name);
						doc.setFilePath(fileName);
						doc.setFinished(true);

						// //UPDATE DOC
						session.getAklaboxService().updateDocument(doc);

						Versions docVersion = new Versions();
						docVersion.setDocId(doc.getId());
						docVersion.setDocRelPath(doc.getFilePath());
						docVersion.setModifiedBy(doc.getLastModifiedBy());
						docVersion.setVersionNumber(1);
						docVersion.setVersionFileSize(doc.getFileSize());
						docVersion.setDocName(doc.getFileName());
						docVersion.setHadoopFile(Boolean.parseBoolean(ConfigurationManager.getInstance().getVanillaConfiguration().getProperty("bpm.hdfs.use")));

						session.getAklaboxService().saveDocVersion(docVersion);
						System.out.println("Document processed");
					} catch (Exception e) {
						e.printStackTrace();
						try {
							throw new Exception(e.getMessage());
						} catch (Exception e1) {
							e1.printStackTrace();
						}
					}
				}
				else if (type.equals(ScannerIOMetadata.NEGOTIATE)) {
					System.out.println("Applying settings");
					try {
						ScannerDevice device = metadata.getDevice();
						try {
							device.setShowUserInterface(true);
							device.setShowProgressBar(true);
							device.setResolution(100);
						} catch (Exception e) {
							e.printStackTrace();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				else if (type.equals(ScannerIOMetadata.STATECHANGE)) {
					System.out.println("Scan state change");
//					System.err.println(metadata.getStateStr());
					System.out.println(metadata.getStateStr());
				}
				else if (type.equals(ScannerIOMetadata.EXCEPTION)) {
					System.out.println("Scan exception");
					System.out.println(metadata.getException());
				
					metadata.getException().printStackTrace();
					try {
						throw new Exception(metadata.getException().getMessage());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

//				if (metadata.isFinished()) {
//					scanner.waitToExit();
//				}
				// twainScanner = (TwainScanner) Scanner.getDevice();
				// }
			}
		});
		scanner.acquire();
	}
}
