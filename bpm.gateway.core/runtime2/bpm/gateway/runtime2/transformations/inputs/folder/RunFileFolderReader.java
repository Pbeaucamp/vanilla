package bpm.gateway.runtime2.transformations.inputs.folder;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import bpm.gateway.core.transformations.files.FileFolderReader;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.internal.Row;
import bpm.gateway.runtime2.internal.RowFactory;
import bpm.gateway.runtime2.tools.StringParser;

public class RunFileFolderReader extends RuntimeStep{
	
	private IFileReader fileReader;
	
	private List<File> files = new ArrayList<File>();
	private Iterator<File> iterator;
	
	
	public RunFileFolderReader(FileFolderReader transformation, int bufferSize) {
		super(null, transformation, bufferSize);
	}

	@Override
	public void init(Object adapter) throws Exception {
		FileFolderReader transfo = ((FileFolderReader)getTransformation());
		/*
		 * get files
		 */
		String folderPath = null;
		try{
			folderPath = getTransformation().getDocument().getStringParser().getValue(getTransformation().getDocument(), transfo.getFolderPath());
		}catch(Exception ex){
			ex.printStackTrace();
			throw new Exception("Unable to parse the folderPath:" + ex.getMessage(), ex);
		}
		
		File f = new File(folderPath);
		if (!f.exists()){
			throw new Exception("the Folder " + folderPath + " does not exist");
		}
		
		if (!f.isDirectory()){
			throw new Exception(folderPath + " is not a Folder");
		}
		for(String s : f.list()){
			File _f = new File(f, s);
			
			if (!_f.isFile()){
				continue;
			}
			
			if (s.matches(transfo.getFileNamePattern().replace("*", ".*"))){
			
				files.add(_f);
			}
		}
	}

	@Override
	public void performRow() throws Exception {
		
		
		IFileReader reader = null;
		
		try{
			reader = getReader();
		}catch(Exception ex){
			if (iterator.hasNext()){
				error(ex.getMessage(), ex);
				warn("A file has  been skipped du to an error");
				return;
			}
			else{
				setEnd();
				return;
			}
		}
		
		Row row = reader.readRow();
		
		if (row == null){
			return;
		}
		Row newRow = RowFactory.createRow(this, row);
		
		
		writeRow(newRow);
		
	}

	@Override
	public void releaseResources() {
		fileReader.releaseResources();
		
	}
	
	protected IFileReader getReader() throws Exception{
		if (fileReader != null && !fileReader.isAllRead()){
			return fileReader;
		}
		
		if (iterator == null){
			iterator = files.iterator();
		}
		
		File f = iterator.next();
		
		
		try{
			switch(((FileFolderReader)getTransformation()).getFileType()){
			case CSV:
				fileReader = new  CsvFileReader(this, f, ((FileFolderReader)getTransformation()).getEncoding(), ((FileFolderReader)getTransformation()).getCsvSeparator(), ((FileFolderReader)getTransformation()).isSkipFirstRow());
				info("CSV File Reader created on " + f.getAbsolutePath());
				break;
				
			case XLS:
				fileReader = new  XlsFileReader(this, f, ((FileFolderReader)getTransformation()).getXlsSheetName(), ((FileFolderReader)getTransformation()).getEncoding(), ((FileFolderReader)getTransformation()).isSkipFirstRow());
				info("XLS File Reader created on " + f.getAbsolutePath());
				break;
				
			case XML:
				fileReader = new  XmlFileReader(this, f, ((FileFolderReader)getTransformation()).getEncoding(), ((FileFolderReader)getTransformation()).getXmlRootTag(), ((FileFolderReader)getTransformation()).getXmlRowTag());
				info("XML File Reader created on " + f.getAbsolutePath());
				break;

			}
		}catch(Exception ex){
			throw new Exception("Unable to create reader on " + f.getAbsolutePath() + " : " + ex.getMessage(), ex);
		}
		
		return fileReader;
		
	}
	
}
