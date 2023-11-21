package bpm.test;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;

import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSelector;
import org.apache.commons.vfs.FileSystemOptions;
import org.apache.commons.vfs.VFS;
import org.apache.commons.vfs.impl.DefaultFileSystemManager;
import org.apache.commons.vfs.provider.sftp.SftpFileSystemConfigBuilder;



public class TestSFTP {

	public static void main(String[] args) {
		String host = "ns203301.ovh.net";
		String login = "bpm";
		String password = "bpmrulez";


		try {
			// we first set strict key checking off
			FileSystemOptions fsOptions = new FileSystemOptions();
			SftpFileSystemConfigBuilder.getInstance().setStrictHostKeyChecking(
					fsOptions, "no");
			// now we create a new filesystem manager
			DefaultFileSystemManager fsManager = (DefaultFileSystemManager) VFS
			.getManager();
			// the url is of form sftp://user:pass@host/remotepath/
			String uri = "sftp://" + login + ":" + password + "@" + host + "/" + "home/bpm/";
			// get file object representing the local file
			
			FileObject fo = fsManager.resolveFile(uri, fsOptions);
			
			FileObject[] childs = fo.getChildren();
			for (int i = 0; i < childs.length; i++) {
				System.out.println(childs[i].getName().getBaseName());
			}
//			// open input stream from the remote file
//			BufferedInputStream is = new BufferedInputStream(fo.getContent()
//					.getInputStream());
//			// open output stream to local file
//			OutputStream os = new BufferedOutputStream(new FileOutputStream("c:\\sftp.txt"));
//			int c;
//			// do copying
//			while ((c = is.read()) != -1) {
//				os.write(c);
//			}
//			os.close();
//			is.close();
//			// close the file object
//			fo.close();
//			// NOTE: if you close the file system manager, you won't be able to 
//			// use VFS again in the same VM. If you wish to copy multiple files,
//			// make the fsManager static, initialize it once, and close just
//			// before exiting the process.
			fsManager.close();
			System.out.println("Finished copying the file");

		}
		catch (Exception e) {
			e.printStackTrace();
		}


	}


}
