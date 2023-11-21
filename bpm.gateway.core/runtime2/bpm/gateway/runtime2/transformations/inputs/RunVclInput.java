package bpm.gateway.runtime2.transformations.inputs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

import bpm.gateway.core.server.file.AbstractFileServer;
import bpm.gateway.core.transformations.inputs.FileInputVCL;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.internal.Row;
import bpm.gateway.runtime2.internal.RowFactory;

public class RunVclInput extends RuntimeStep{

	
	private BufferedReader reader;
	private String line;
	private int rowLength = 0;
	
	private boolean handleError = false;
	private RuntimeStep errorHandler;
	private List<Integer> colsSizes;
	
	public RunVclInput(FileInputVCL transformation, int bufferSize) {
		super(null, transformation, bufferSize);
		
	}

	@Override
	public void init(Object adapter) throws Exception {
		FileInputVCL vcl = (FileInputVCL)getTransformation();
		
		
		
		if (vcl.getTrashTransformation() != null){
			for(RuntimeStep rs : getOutputs()){
				if (rs.getTransformation() == vcl.getTrashTransformation()){
					handleError = true;
					errorHandler = rs;
					break;
				}
			}
		}
		
		Reader in = new InputStreamReader(((AbstractFileServer)vcl.getServer()).getInpuStream(vcl), vcl.getEncoding());
		reader = new BufferedReader(in);
		info(" FileReader created");
		rowLength = vcl.getDescriptor(vcl).getColumnCount();
		colsSizes = vcl.getColumnSizes();
		for(int i = 0; i < vcl.getNumberOfRowToSkip(); i++){
			reader.readLine();
		}
		info("Skipped the " + vcl.getNumberOfRowToSkip() + " first rows");
	}

	@Override
	public void performRow() throws Exception {
		line = reader.readLine();
		
		if (line == null){
			setEnd();
			return;
		}
		
		
		readedRows ++;
		
		
		Row row = RowFactory.createRow(this);
		
		
		int offset = 0;
		for(int i = 0; i < rowLength ; i++){
			String _val = null;
			
			try{
				if (offset + colsSizes.get(i) < line.length()){
					_val = line.substring(offset, offset + colsSizes.get(i));
					offset += colsSizes.get(i) +1;
				}
				else{
					_val = line.substring(offset);
				}
				_val = _val.trim();
			}catch(Exception ex){
				row.set(i, null);
				continue;
			}
			
			
			if (_val.equals("")){
				row.set(i, null);
				continue;
			}
			
			
			try{
				if (_val.startsWith("0")){
					row.set(i, _val);
				}
				else{
					row.set(i, Integer.parseInt(_val));
				}
				
				continue;
			}catch(NumberFormatException ex){
				
			}
			
			try{
				row.set(i, Float.parseFloat(_val));
				continue;
			}catch(NumberFormatException ex){
				
			}
			
			try{
				row.set(i, Double.parseDouble(_val));
				continue;
			}catch(NumberFormatException ex){
				
			}
			
			
			
			try{
				if (isBoolean(_val)){
					row.set(i, Boolean.parseBoolean(_val));
					continue;
				}
										
			}catch(NumberFormatException ex){
				
			}
			
			
			row.set(i, _val);
		}
		
	
		
		writeRow(row);
		
	}

	@Override
	public void releaseResources() {
		try {
			reader.close();
			info(" FileReader closed");
		} catch (IOException e) {
			error( " problem when closing FileReader", e);
		}
		
	}
	
	@Override
	protected void writeRow(Row row) throws InterruptedException{
		boolean wrote = false;
		for(RuntimeStep r : getOutputs()){
			if (r != errorHandler){
				r.insertRow(row, this);
				wrote = true;
			}
			
			
		}
		if (wrote){
			writedRows++;
		}
		
	}
	
	protected void writeErrorRow(Row row) throws InterruptedException{
		if (errorHandler != null){
			errorHandler.insertRow(row, this);
			writedRows++;
		}
	}

	private boolean isBoolean(String s){
		if (s == null || "true".equalsIgnoreCase(s.trim()) || "false".equalsIgnoreCase(s.trim())
				||
			"1".equalsIgnoreCase(s.trim()) || "0".equals(s.trim())){
			return true;
		}
		return false;
	}
}
