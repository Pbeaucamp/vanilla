package bpm.gwt.aklabox.commons.server.scan;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

import javax.imageio.ImageIO;

import bpm.document.management.core.model.Documents;
import bpm.document.management.core.model.Versions;
import bpm.gwt.aklabox.commons.server.security.CommonSession;
import bpm.vanilla.platform.core.config.ConfigurationManager;

import com.asprise.imaging.core.Imaging;
import com.asprise.imaging.core.Result;

public class JTwainAkLad {

//	private static Logger logger = Logger.getLogger(JTwainAkLad.class);

//	public static void main(String[] args) {
//		try {
//			scan();
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}

	public static void scan(final CommonSession session, final Documents doc, final String aklaboxFilesPath) throws Exception {
		System.out.println("Starting scan");

		final Imaging imaging = new Imaging(null); // owner can be null
		Imaging.getDefaultExecutorServiceForScanning().submit(new Runnable() {
			public void run() {
				try {
					String json = "{ \"output_settings\" : [ " + "{ \"type\" : \"save\", \"format\" : \"jpg\", " + "\"save_path\" : \"C:/BPM/Test/${TMS}${EXT}\" } ] }";
					Result result = imaging.scan(json, "select", false, true);
					List<BufferedImage> files = result.getImages();
					try {
						String name = doc.getName() + System.currentTimeMillis() + "." + doc.getFileExtension();
						String fileName = aklaboxFilesPath + "Documents/Images/" + name;

						File f = new File(fileName);
						ImageIO.write(files.get(0), "jpg", f);

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
					System.out.println("Got image");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
