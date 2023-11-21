package bpm.gateway.core.tools.database;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.xml.sax.SAXException;

public class DBConverterManager {


	private List<DBConverter> converters = new ArrayList<DBConverter>();
	
	public DBConverterManager(String folderName){
		File folder = new File(folderName);
		
		for(File f : folder.listFiles()){
			if (f.getAbsolutePath().endsWith(".xml")){
				
				DBConverter c;
				try {
					c = DBConverterLoader.loadFromFile(f.getAbsolutePath(), "conversionMapping");
					converters.add(c);
				} catch (IOException e) {
					
					e.printStackTrace();
				} catch (SAXException e) {
					
					e.printStackTrace();
				}

			}
		}
		
	}
	
	
	
	public List<String> getConvertersNames(){
		List<String> l = new ArrayList<String>();
		
		for(DBConverter c : converters){
			l.add(c.getJdbcDriverName());
		}
		
		return l;
		
	}
	
	public String getTypeSyntax(String targetType, String columnType) throws Exception{
		
		for(DBConverter conv : converters){
			if (conv.getJdbcDriverName().equals(targetType)){
				
				return conv.getTypeName(columnType);
							
				
			}
			
		}
		
		
		return "";
	
	}



	public boolean hasPrecision(String targetType, String typeName) {
		for(DBConverter conv : converters){
			if (conv.getJdbcDriverName().equals(targetType)){
				
				return conv.hasPrecision(typeName);
							
				
			}
			
		}
		return false;
	}
	
	
	
}
