package bpm.gateway.runtime2.transformations.outputs;

import java.io.File;
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.util.Date;

import bpm.gateway.core.StreamElement;
import bpm.gateway.core.server.d4c.D4CServer;
import bpm.gateway.core.server.file.MdmFileServer;
import bpm.gateway.core.tools.Utils;
import bpm.gateway.core.transformations.outputs.FileOutputCSV;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.internal.DateFormaterTool;
import bpm.gateway.runtime2.internal.Row;
import bpm.vanilla.platform.core.utils.D4CHelper;
import bpm.vanilla.platform.core.utils.D4CHelper.D4CResult;
import bpm.vanilla.platform.core.utils.D4CHelper.Status;

public class RunCSVOutput extends RuntimeStep{
	
	private FileOutputCSV csv;
	
	private String separator;
	private PrintWriter writer;

	private File file = null;

	public RunCSVOutput(FileOutputCSV transformation, int bufferSize) {
		super(null, transformation, bufferSize);
	}

	@Override
	public void init(Object adapter) throws Exception {
		this.csv = (FileOutputCSV)getTransformation();
		this.separator = "" + csv.getSeparator();

		// flag to decide if the Headers Should be Writed or not
		boolean fileCreated = false;
		if (csv.useMdm()) {
			file = Utils.createTmpFile(null);
			fileCreated = true;
		}
		else {
			String fileName = null;
			try{
				fileName = csv.getDocument().getStringParser().getValue(getTransformation().getDocument(), csv.getDefinition());
			}catch(Exception e){
				error(" error when getting/parsing fileName", e);
				throw e;
			}
			
			file = new File(fileName);
			if(csv.getDelete() && file.exists()){
				file.delete();
				info(" delete file " + file.getAbsolutePath());
			}
			
			file = new File(fileName);
			if (!file.exists()){
				try{
					file.createNewFile();
					fileCreated = true;
					info( " file " + file.getAbsolutePath() + " created");
				}catch(Exception e){
					error(" cannot create file " + file.getName(), e);
					throw e;
				}
			}
		}
		
		try{
			writer = new PrintWriter(file, csv.getEncoding());
			info( " Writer created");
		}catch (Exception e) {
			error(" cannot create writer", e);
		}
		
		if (csv.isContainHeader()){
			try{
				writeHeader();
				info( " CSV Header writen");
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
		boolean first = true;
		for(Object o : row){
			if(first) {
				first = false;
			}
			else {
				buf.append(separator);
			}
			if ( o == null){
				
			}
			else if ( o instanceof Date){
				buf.append("\"" + DateFormaterTool.format((Date)o) + "\"");
			}
			else{
				buf.append("\"" + o.toString().replace("\"", "\"\"") + "\"");
			}
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
		
		String fileName = csv.getName() + ".csv";

		if (csv != null && csv.useMdm()) {
			uploadToMDM(fileName);
		}
		// Add condition
		else if (csv != null && csv.useD4C()) {
			uploadToD4C(fileName, "csv");
		}
	}
	
	private void uploadToMDM(String fileName) {
		try (FileInputStream fileStream = new FileInputStream(file)) {
			((MdmFileServer) csv.getFileServer()).uploadFileToMDM(csv.getName() + ".csv", csv.getContractId(), fileStream);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void uploadToD4C(String fileName, String format) {
		String datasetName = csv.getDefinition();
		
		D4CServer server = (D4CServer) csv.getServer();
		String org = server.getOrg();

		try (FileInputStream fileStream = new FileInputStream(file)) {
			D4CHelper helper = server.getD4CHelper();
			
			D4CResult result = helper.uploadFileResourceAndCreatePackageIfNeeded(org, datasetName, null, fileName, fileStream, null, null, true, true, format);
			if (result.getStatus() == Status.ERROR) {
				throw new Exception(result.getMessage());
			}
		} catch (Exception e) {
			e.printStackTrace();
			
			error(" error when uploading file to D4C", e);
		}
	}
	
	private void writeHeader() throws Exception{
		boolean first = true;
		for(StreamElement e : getTransformation().getDescriptor(getTransformation()).getStreamElements()){
			if(first) {
				first = false;
				writer.write(e.name);
			}
			else {
				writer.write(separator + e.name);
			}
			
		}
		writer.write("\r\n");
	}

}
