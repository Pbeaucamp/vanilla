package bpm.vanillahub.runtime.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipHelper {

	public static void unzip(InputStream bis, String targetFolderPath) throws IOException {

		byte[] buffer = new byte[1024];

		try (ZipInputStream zis = new ZipInputStream(bis)) {
			ZipEntry ze = zis.getNextEntry();
			while (ze != null) {

				String fileName = ze.getName();
				File newFile = new File(targetFolderPath + fileName);
//				logger.debug("File unzip : " + newFile.getAbsoluteFile());

				new File(newFile.getParent()).mkdirs();

				if (ze.isDirectory()) {
					newFile.mkdir();
				}
				else {
					FileOutputStream fos = new FileOutputStream(newFile);

					int len;
					while ((len = zis.read(buffer)) > 0) {
						fos.write(buffer, 0, len);
					}

					fos.close();
				}
				ze = zis.getNextEntry();
			}

			zis.closeEntry();
		}
	}
}
