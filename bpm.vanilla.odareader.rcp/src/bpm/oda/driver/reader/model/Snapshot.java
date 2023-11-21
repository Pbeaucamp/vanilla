package bpm.oda.driver.reader.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.eclipse.core.runtime.Platform;

import bpm.oda.driver.reader.actions.ActionExport;

public class Snapshot implements ILabelable	 {

	private String fileName;
	private String dataSetName;
	
	public final static String SNAP_NAME_SEPARATOR = "_";
	
	private List<List<String>> listValues;
	private List<String> listColumns;
	
	public Snapshot(String fileName, String dataSetName) {
		super();
		this.fileName = fileName;
		this.dataSetName = dataSetName;
	}
	
	public boolean fillValues(){
		
		boolean isFirstLine  = true;
		listColumns = new ArrayList<String>();
		listValues = new ArrayList<List<String>>();
		
		
	//File
		String path = Platform.getInstanceLocation().getURL().getPath();
		File file = new File(path, "temp");
		file = new File(file, this.fileName + ".txt"); 
		
		Scanner scanner = null;
		try {
			scanner = new Scanner(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		}

		while (scanner.hasNextLine()) {
		    String line = scanner.nextLine();
		    
		    List<String> lineValues = new ArrayList<String>();
		    
		    //Test if is it the first line > Columns
		    if(isFirstLine){
		    	for(String cell : line.split(ActionExport.CSV_SEPARATOR)){
		    		listColumns.add(cell);
		    	}
		    	
		    	isFirstLine = false;
		    }
		    
		    else{
		    	for(String cell : line.split(ActionExport.CSV_SEPARATOR)){
		    		lineValues.add(cell);
		    	}
		    	
		    	listValues.add(lineValues);
		    }
		    
		}

		scanner.close();

		return true;
		
		
	}
	
	
	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}


	public String getName() {
		return fileName;
	}


	public String getDataSetName() {
		return dataSetName;
	}


	public void setDataSetName(String dataSetName) {
		this.dataSetName = dataSetName;
	}

	public List<List<String>> getListValues() {
		return listValues;
	}

	public void setListValues(List<List<String>> listValues) {
		this.listValues = listValues;
	}

	public List<String> getListColumns() {
		return listColumns;
	}

	public void setListColumns(List<String> listColumns) {
		this.listColumns = listColumns;
	}

}
