package bpm.vanillahub.runtime.specifics;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.json.JSONException;

import bpm.vanillahub.runtime.specifics.onisep.Metiers;

/**
 * This class is design to explode a JSON coming from AGEFMA Carif
 * 
 * Separate in 3 file - organismeformation - actionformation - sessionformation
 * 
 */
public class OnisepMetierXMLExploder {

	private static final String CSV_SEPARATOR = ";";

	/**
	 * This program take 3 arguments
	 *  1. source file path
	 *  2. target folder path
	 * 
	 * @param args
	 * @throws Exception 
	 */
	public static void onisep(String[] args) throws Exception {
		if (args.length < 3) {
			throw new Exception("Arguments are not valid. Should be type, source, target.");
		}
		
		String source = args[1];
		String target = args[2];
		
		//We add a slash at the end if not present
		target = target.endsWith("/") ? target : target+ "/" ;
		
		try (FileInputStream inputStream = new FileInputStream(new File(source))) {
			explodeXML(target, inputStream);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static final void explodeXML(String target, InputStream inputStream) throws JSONException, JAXBException {
//		Reader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);

		JAXBContext jaxbContext     = JAXBContext.newInstance( Metiers.class );
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		 
		//Overloaded methods to unmarshal from different xml sources
		Metiers metiers = (Metiers) jaxbUnmarshaller.unmarshal( inputStream );
		
		HashMap<String, List<List<Object>>> exportCsv = new HashMap<String, List<List<Object>>>();
		metiers.buildCSV(null, exportCsv);
		
        for (String key : exportCsv.keySet()) {
			List<List<Object>> values = exportCsv.get(key);
			if (values != null && !values.isEmpty()) {

	    		// Write CSV file
	    		try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(target + key + ".csv"), StandardCharsets.UTF_8))) {
    			
    				for (List<Object> line : values) {
    					StringBuffer oneLine = new StringBuffer();
    					boolean first = true;
    					for (Object item : line) {
    						if (!first) {
    			                oneLine.append(CSV_SEPARATOR);
    						}
    						
    						String value = cleanValue(item.toString());
    		                oneLine.append("\"" + value + "\"");
    		                first = false;
    					}
    	    			
    	    			bw.write(oneLine.toString());
    	                bw.newLine();
    				}
                
	                bw.flush();
	                bw.close();
	    		} catch (IOException e) {
	    			e.printStackTrace();
	    		}
			}
		}
	}

	private static String cleanValue(String value) {
		value = value.replace("\"", "\"\"");
		return value;
	}
}
