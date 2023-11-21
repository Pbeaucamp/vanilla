package bpm.gwt.aklabox.commons.server.hdfs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.security.PrivilegedExceptionAction;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.security.UserGroupInformation;

import bpm.vanilla.platform.core.config.ConfigurationManager;

public class HdfsHelper {

	private static String addDocument(final String originPath, final String path, final String format) throws Exception {
		// Find the directory and file paths
		try {
			UserGroupInformation ugi = UserGroupInformation.createRemoteUser(ConfigurationManager.getInstance().getVanillaConfiguration().getProperty("bpm.hdfs.user"));

			ugi.doAs(new PrivilegedExceptionAction<Void>() {

				public Void run() throws Exception {

					// init the fs
					Configuration conf = new Configuration();

					conf.set("fs.defaultFS", ConfigurationManager.getInstance().getVanillaConfiguration().getProperty("bpm.hdfs.default.fs"));
					conf.set("hadoop.job.ugi", ConfigurationManager.getInstance().getVanillaConfiguration().getProperty("bpm.hdfs.user"));

					File workaround = new File(".");
					System.getProperties().put("hadoop.home.dir", workaround.getAbsolutePath());
					new File("./bin").mkdirs();
					new File("./bin/winutils.exe").createNewFile();

					FileSystem fs = FileSystem.get(conf);

					// create the folders
					String userPath = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty("bpm.hdfs.user.path");

					// copy the file to hdfs
					fs.copyFromLocalFile(false, true, new Path(originPath), new Path(userPath + "/" + path));
					return null;
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}

		return path;

	}

	public static String addDocument(final InputStream inputStream, final String path, final String format) throws Exception {
		FileOutputStream fos = new FileOutputStream("webapps/aklabox_files/Templates/temp." + format);
		byte buffer[] = new byte[512 * 1024];
		int nbLecture;

		while ((nbLecture = inputStream.read(buffer)) != -1) {
			fos.write(buffer, 0, nbLecture);

		}
		inputStream.close();
		fos.close();

		return addDocument("webapps/aklabox_files/Templates/temp." + format, path, format);
	}

	public static InputStream loadFileFromHDFS(final String path) throws Exception {
		final String format = path.substring(path.lastIndexOf("."));
		try {
			UserGroupInformation ugi = UserGroupInformation.createRemoteUser(ConfigurationManager.getInstance().getVanillaConfiguration().getProperty("bpm.hdfs.user"));

			ugi.doAs(new PrivilegedExceptionAction<Void>() {

				public Void run() throws Exception {

					// init the fs
					Configuration conf = new Configuration();

					conf.set("fs.defaultFS", ConfigurationManager.getInstance().getVanillaConfiguration().getProperty("bpm.hdfs.default.fs"));
					conf.set("hadoop.job.ugi", ConfigurationManager.getInstance().getVanillaConfiguration().getProperty("bpm.hdfs.user"));

					File workaround = new File(".");
					System.getProperties().put("hadoop.home.dir", workaround.getAbsolutePath());
					new File("./bin").mkdirs();
					new File("./bin/winutils.exe").createNewFile();

					FileSystem fs = FileSystem.get(conf);

					String userPath = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty("bpm.hdfs.user.path");

					fs.copyToLocalFile(false, new Path(userPath + path), new Path("webapps/aklabox_files/Load/load" + format), true);

					return null;
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new FileInputStream("webapps/aklabox_files/Load/load" + format);
	}

	public static void deleteFileFromHdfs(final String path) {
		try {
			UserGroupInformation ugi = UserGroupInformation.createRemoteUser(ConfigurationManager.getInstance().getVanillaConfiguration().getProperty("bpm.hdfs.user"));

			ugi.doAs(new PrivilegedExceptionAction<Void>() {

				public Void run() throws Exception {

					// init the fs
					Configuration conf = new Configuration();

					conf.set("fs.defaultFS", ConfigurationManager.getInstance().getVanillaConfiguration().getProperty("bpm.hdfs.default.fs"));
					conf.set("hadoop.job.ugi", ConfigurationManager.getInstance().getVanillaConfiguration().getProperty("bpm.hdfs.user"));

					File workaround = new File(".");
					System.getProperties().put("hadoop.home.dir", workaround.getAbsolutePath());
					new File("./bin").mkdirs();
					new File("./bin/winutils.exe").createNewFile();

					FileSystem fs = FileSystem.get(conf);

					String userPath = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty("bpm.hdfs.user.path");

					fs.delete(new Path(userPath + "/" + path), true);

					return null;
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
