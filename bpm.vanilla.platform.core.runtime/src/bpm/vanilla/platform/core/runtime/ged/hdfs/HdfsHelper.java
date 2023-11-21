package bpm.vanilla.platform.core.runtime.ged.hdfs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.security.PrivilegedExceptionAction;
import java.util.Calendar;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.LocatedFileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.RemoteIterator;
import org.apache.hadoop.security.UserGroupInformation;

import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;

public class HdfsHelper {

	public static String addDocument(final InputStream inputStream, final String format) throws Exception {

		// Find the directory and file paths
		String documentRelativePath = "/ged_documents/" + Calendar.getInstance().get(Calendar.YEAR) + "/" + Calendar.getInstance().get(Calendar.MONTH) + "/";
		String documentName = "document_" + Calendar.getInstance().getTimeInMillis() + "." + format;

		String documentParentDirectory = documentRelativePath;
		final String filePath = documentParentDirectory + documentName;

		try {
			UserGroupInformation ugi = UserGroupInformation.createRemoteUser("hduser");

			ugi.doAs(new PrivilegedExceptionAction<Void>() {

				public Void run() throws Exception {

					//init the fs
					Configuration conf = new Configuration();
					
					conf.set("fs.defaultFS", ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_HDFS_URL));
					conf.set("hadoop.job.ugi", ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_HDFS_USER));

					File workaround = new File(".");
			        System.getProperties().put("hadoop.home.dir", workaround.getAbsolutePath());
			        new File("./bin").mkdirs();
			        new File("./bin/winutils.exe").createNewFile();
					
					FileSystem fs = FileSystem.get(conf);
					
					//create the folders
					String userPath = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_HDFS_USER_PATH);
					
					//create a tmp file
					FileOutputStream fos = new FileOutputStream("temp." + format);
					byte buffer[]=new byte[512*1024]; 
					int nbLecture;
					
					
					while((nbLecture = inputStream.read(buffer)) != -1 ){
						fos.write(buffer, 0, nbLecture);
						
					}
					inputStream.close();
					fos.close();
					
					//copy the file to hdfs
					fs.copyFromLocalFile(false, true, new Path("temp." + format), new Path(userPath + filePath));
					
					return null;
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}

		return filePath;

	}
	
	public static InputStream loadFileFromHDFS(final String path) throws Exception {
		final String format = path.substring(path.lastIndexOf("."));
		try {
			UserGroupInformation ugi = UserGroupInformation.createRemoteUser("hduser");

			ugi.doAs(new PrivilegedExceptionAction<Void>() {

				public Void run() throws Exception {

					//init the fs
					Configuration conf = new Configuration();
					
					conf.set("fs.defaultFS", ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_HDFS_URL));
					conf.set("hadoop.job.ugi", ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_HDFS_USER));

					File workaround = new File(".");
			        System.getProperties().put("hadoop.home.dir", workaround.getAbsolutePath());
			        File file = new File("./bin/winutils.exe");
			        if(!file.exists()){
			        	new File("./bin").mkdirs();
					    new File("./bin/winutils.exe").createNewFile();
			        }
			        
					
					FileSystem fs = FileSystem.get(conf);
					
					//create the folders
					String userPath = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_HDFS_USER_PATH);
					
					
					//copy the file to hdfs

					fs.copyToLocalFile(false, new Path(userPath + path), new Path("load"+format), false);
					
					return null;
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		
		return new FileInputStream("load"+format);
	}

}
