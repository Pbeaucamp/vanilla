package bpm.gateway.runtime2.transformations.outputs;

import java.io.File;
import java.io.PrintWriter;
import java.util.Date;
import java.util.List;

import bpm.gateway.core.StreamElement;
import bpm.gateway.core.transformations.outputs.FileOutputVCL;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.internal.DateFormaterTool;
import bpm.gateway.runtime2.internal.Row;
import bpm.gateway.runtime2.tools.StringParser;

public class RunVCLOutput extends RuntimeStep{

	private List<Integer> columnSizes;
	private PrintWriter writer;
	private boolean truncateFields = true;
	private boolean handleError = false;
	private RuntimeStep errorHandler;
	
	public RunVCLOutput(FileOutputVCL transformation, int bufferSize) {
		super(null, transformation, bufferSize);
		
	}

	@Override
	public void init(Object adapter) throws Exception {
		FileOutputVCL csv = (FileOutputVCL)getTransformation();
		truncateFields = csv.getTruncateField();
		
		if (csv.getTrashTransformation() != null){
			for(RuntimeStep rs : getOutputs()){
				if (rs.getTransformation() == csv.getTrashTransformation()){
					handleError = true;
					errorHandler = rs;
					break;
				}
			}
		}
		
		columnSizes = csv.getColumnSizes();
		String fileName = null;
		try{
			fileName = getTransformation().getDocument().getStringParser().getValue(getTransformation().getDocument(), csv.getDefinition());
		}catch(Exception e){
			error(" error when getting/parsing fileName", e);
			throw e;
		}
		
		File f = new File(fileName);
		if(csv.getDelete() && f.exists()){
			f.delete();
			info(" delete file " + f.getAbsolutePath());
		}
		
		// flag to decide if the Headers Should be Writed or not
		boolean fileCreated = false;
		
		if (!f.exists()){
			try{
				f.createNewFile();
				fileCreated = true;
				info( " file " + f.getAbsolutePath() + " created");
			}catch(Exception e){
				error(" cannot create file " + f.getName(), e);
				throw e;
			}
		}
		
		try{
			
			writer = new PrintWriter(f, csv.getEncoding());

			
			info( " Writer created");
		}catch (Exception e) {
			error(" cannot create writer", e);
		}
		
		if (fileCreated && csv.isContainHeader()){
			try{
				writeHeader();
				info( " VCL Header writen");
			}catch(Exception e){
				error(" error when writing Header", e);
				throw e;
			}
			
		}
		isInited = true;
	}

	@Override
	public void performRow() throws Exception {
		if (areInputStepAllProcessed()){
			if (inputEmpty()){
				setEnd();
			}
		}
		
		if (isEnd() && inputEmpty()){
			return;
		}
		
		if (!isEnd() && inputEmpty()){
			try {
				Thread.sleep(10);
			}
			catch (InterruptedException e) {
				Thread.currentThread().interrupt(); // restore interrupted status
			}
			return;
		}
		
		
		Row row = readRow();
		
		if (row == null){
			return;
		}

		StringBuffer buf = new StringBuffer();
		int counter = 0;
		for(Object o : row){
			
			String _fieldValue = "";
			if ( o == null){
			}
			else if ( o instanceof Date){
				_fieldValue = DateFormaterTool.format((Date)o) + " ";
			}
			else{
				_fieldValue = o.toString() + " ";
			}
			
			if (_fieldValue.length() > columnSizes.get(counter)){
				
				if (truncateFields){
					
					_fieldValue = _fieldValue.substring(0, columnSizes.get(counter)) + " ";
					warn("Data truncated on columnn "+  counter + " : " + o);
					buf.append(_fieldValue);
				}
				else if (handleError){
					writeErrorRow(row);
					warn("Data too long, row skiped and put in the ErrorHandlerStream :" + o);
					return;
				}
				else{
					throw new Exception("Data too long on column " + counter + ": " + o.toString());
				}
			}
			else{
				buf.append(_fieldValue);
				for(int i = _fieldValue.length(); i <= columnSizes.get(counter)  ; i++){
					buf.append(" ");
				}
				
				
			}
			counter++;
			
			
			
		}
		buf.append("\r\n");
		writer.write(buf.toString());
		writeRow(row);
		if (areInputStepAllProcessed()){
			if (inputEmpty()){
				setEnd();
			}
		}
		
	}

	@Override
	public void releaseResources() {
		if (writer != null){
			writer.close();
			info(" close writer");
			writer = null;
		}
		
	}
	
	
	private void writeHeader() throws Exception{
		int counter = 0;
		for(StreamElement e : getTransformation().getDescriptor(getTransformation()).getStreamElements()){
			writer.write(e.name );
			for(int i = e.name.length(); i <= columnSizes.get(counter); i++){
				writer.write(" ");
			}
			counter++;
		}
		writer.write("\r\n");
	}
	
	protected void writeErrorRow(Row row) throws InterruptedException{
		if (errorHandler != null){
			errorHandler.insertRow(row, this);
			writedRows++;
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

}
