package bpm.gateway.core.tools;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class Utils {

	/**
	 * Create a file and write an {@link InputStream} to a file
	 * (The InputStream can be null to create an empty file)
	 * 
	 * @param fileStream (can be null)
	 * @return the file
	 */
	public static File createTmpFile(InputStream fileStream) {
		return createTmpFile(fileStream, "tmp");
	}

	/**
	 * Create a file and write an {@link InputStream} to a file
	 * (The InputStream can be null to create an empty file)
	 * 
	 * @param fileStream (can be null)
	 * @param format (the extension at the end of the file)
	 * @return the file
	 */
	public static File createTmpFile(InputStream fileStream, String format) {
		try {
			File tmpFile = File.createTempFile("TmpFile_" + new Object().hashCode(), "." + format);
			if (fileStream != null) {
				Files.copy(fileStream, Paths.get(tmpFile.getPath()), StandardCopyOption.REPLACE_EXISTING);
			}
			return tmpFile;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
