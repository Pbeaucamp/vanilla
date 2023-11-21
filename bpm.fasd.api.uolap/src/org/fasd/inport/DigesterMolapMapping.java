package org.fasd.inport;

import java.io.FileNotFoundException;
import java.io.FileReader;

import org.apache.commons.digester3.Digester;
import org.fasd.molap.MOLAPMappingContext;



public class DigesterMolapMapping {

private MOLAPMappingContext context;
	
	public DigesterMolapMapping (String path) throws FileNotFoundException, Exception {
		FileReader f = new FileReader(path);
		
		Digester dig = new Digester();
		dig.setValidating(false);

		String root = "mapping";
		
		dig.addObjectCreate(root, MOLAPMappingContext.class);
		
		dig.addCallMethod(root + "/mapping-item", "addItem", 3);
		dig.addCallParam(root + "/mapping-item/dataobject-id", 0);
		dig.addCallParam(root + "/mapping-item/molap-dataobject-id", 1);
		dig.addCallParam(root + "/mapping-item/file", 2);
		
		
		try {
			context = (MOLAPMappingContext) dig.parse(f);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			//System.out.println("NB Dims = " + schema.getDimensions().size());
		}
	}
	
	public MOLAPMappingContext getContext(){
		return context;
	}
}
