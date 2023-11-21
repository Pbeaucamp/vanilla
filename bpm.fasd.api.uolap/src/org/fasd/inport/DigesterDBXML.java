package org.fasd.inport;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;

import org.apache.commons.digester3.Digester;

import bpm.studio.jdbc.management.model.DriverInfo;



public class DigesterDBXML {
//	private ArrayList list;
//	private Digester dig;
//	
//	private DigesterDBXML(){
//		initDigester();
//		registerCallBacks();
//	}
//	
//	public DigesterDBXML(String path) throws FileNotFoundException, Exception {
//		this();
//		
//		FileReader f = new FileReader(path);
//		try {
//			list = (ArrayList) dig.parse(f);
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//			throw new Exception(e.getMessage());
//		} finally {
//			//System.out.println("NB Dims = " + schema.getDimensions().size());
//		}
//	}
//	
//	private void initDigester(){
//		dig = new Digester();
//		dig.setValidating(false);
//	}
//	
//	private void registerCallBacks(){
//		String root = "drivers";
//		
//		dig.addObjectCreate(root, ArrayList.class);
//		
//		dig.addObjectCreate(root + "/driver", DriverInfo.class);
//		dig.addSetProperties(root + "/driver", "name", "name");
//		dig.addSetProperties(root + "/driver", "file", "file");
//		dig.addSetProperties(root + "/driver", "className", "className");
//		dig.addSetProperties(root + "/driver", "prefix", "urlPrefix");
//		dig.addSetNext(root + "/driver", "add");
//	}
//	
//	public ArrayList<DriverInfo> getListDriver() {
//		return list;
//	}
}
