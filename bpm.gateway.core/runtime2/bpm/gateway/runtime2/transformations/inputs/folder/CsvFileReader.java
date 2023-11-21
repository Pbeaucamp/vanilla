package bpm.gateway.runtime2.transformations.inputs.folder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.internal.Row;
import bpm.gateway.runtime2.internal.RowFactory;

public class CsvFileReader implements IFileReader{

	private String separator;
	
	private BufferedReader reader;
	private String line ;
	private RuntimeStep runtimeStep;
	
	public CsvFileReader(RuntimeStep runtimeStep, File file, String encoding, String separator, boolean skpiFirstRow) throws Exception{
		this.runtimeStep = runtimeStep;
		this.separator = separator;
		try{
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), encoding));
		}catch(Exception ex){
			ex.printStackTrace();
			throw new Exception("Unable to create reader on " + file.getAbsolutePath() + ":" + ex.getMessage(), ex);
		}
		if (skpiFirstRow){
			line = reader.readLine();
		}
		line = reader.readLine();
	}

	

	public Row readRow() throws Exception{
		
		
		Row row = RowFactory.createRow(runtimeStep);
		
		
		try{
			String[] lineSplit = line.split("\\" + separator);
			for(int i = 0; i < lineSplit.length - 1; i++){
				if (!"".equals(lineSplit[i])){
					row.set(i, lineSplit[i]);
				}
				
			}
		}catch(IndexOutOfBoundsException ex){
			throw ex;
		}
		line = reader.readLine();
		return row;
	}

	public void releaseResources() {
		try {
			reader.close();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
	}



	public boolean isAllRead() {
		return line == null;
	}
	
}
