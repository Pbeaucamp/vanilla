package bpm.united.olap.runtime.cache;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;


import bpm.united.olap.api.result.OlapResult;

public class OlapResultMarshaller {

	private static class OsgiObjectInputStream extends ObjectInputStream{

		public OsgiObjectInputStream(InputStream in) throws IOException {
			super(in);
			
		}
		
		@Override
		public Class<?> resolveClass(ObjectStreamClass desc) throws IOException,ClassNotFoundException {
			ClassLoader currentTccl = null;
			try {
			
				currentTccl = Thread.currentThread().getContextClassLoader();
				return currentTccl.loadClass(desc.getName());

			} catch (Exception e) {}
			return super.resolveClass(desc);
		}
	}
	
	
	
	
	public OlapResult unmarshall(File f) throws Exception{
		FileInputStream fis = new FileInputStream(f);
		ObjectInputStream in = new OsgiObjectInputStream(fis);
		OlapResult res = (OlapResult)in.readObject();
		in.close();
		return res;
	}
	
	public void marshall(File f, OlapResult result) throws Exception{
		FileOutputStream fos = new FileOutputStream(f);
		ObjectOutputStream out = new ObjectOutputStream(fos);
		out.writeObject(result);
		out.close();
	}
}
