package bpm.vanilla.platform.core.utils;

import java.io.InputStream;
import java.io.OutputStream;

public class IOWriter {

	
	/**
	 * read the content from the input and write it to the output
	 * 
	 * boolean are use to close or not the streams once the job is done
	 * 
	 * @param is
	 * @param os
	 * @param closeInput
	 * @param closeOutput
	 * @throws Exception
	 */
	public static void write(InputStream is, OutputStream os, boolean closeInput, boolean closeOutput) throws Exception{
		
		int sz = 0;
		byte[] buf = new byte[1024];
		
		while( (sz = is.read(buf)) >= 0){
			os.write(buf, 0, sz);
			
		}
		
		
		if (closeInput){
			is.close();
		}
		if (closeOutput){
			os.close();
		}
	}
}
