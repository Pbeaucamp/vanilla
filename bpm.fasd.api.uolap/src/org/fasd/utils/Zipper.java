package org.fasd.utils;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.Adler32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;




public class Zipper {
	
	static final int BUFFER = 2048;
		
	public void zip (String location, String name) {
	   	try {
			FileOutputStream dest = new FileOutputStream(location + "\\" + name + ".zip");
			CheckedOutputStream checksum = new CheckedOutputStream(dest, new Adler32());
			BufferedOutputStream buff = new BufferedOutputStream(checksum);
			ZipOutputStream out = new ZipOutputStream(buff);
			
			out.setMethod(ZipOutputStream.DEFLATED);
			out.setLevel(Deflater.BEST_COMPRESSION);
			
	        
			byte data[] = new byte[BUFFER];
	        
			File f = new File(location);
			String files[] = f.list();
			for (int i=0; i<files.length; i++) {
				if (!files[i].equals(name)){
					FileInputStream fi = new FileInputStream(location + "\\" + files[i]);
		            BufferedInputStream buffi = new BufferedInputStream(fi, BUFFER);
		            ZipEntry entry = new ZipEntry(files[i]);
		            out.putNextEntry(entry);
		            int count;
		            while((count = buffi.read(data, 0, BUFFER)) != -1) {
		               out.write(data, 0, count);
		            }
		            out.closeEntry();
	         		buffi.close();
					}
					
			}
			
						
			out.close();
			buff.close();
			checksum.close();
			dest.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
      
   }
}

