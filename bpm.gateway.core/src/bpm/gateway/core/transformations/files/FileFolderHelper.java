package bpm.gateway.core.transformations.files;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import bpm.gateway.core.DefaultStreamDescriptor;
import bpm.gateway.core.server.file.FileCSVHelper;
import bpm.gateway.core.server.file.FileXLSHelper;
import bpm.gateway.core.server.file.FileXMLHelper;
import bpm.gateway.core.transformations.inputs.FileInputCSV;
import bpm.gateway.core.transformations.inputs.FileInputXLS;
import bpm.gateway.core.transformations.inputs.FileInputXML;


public class FileFolderHelper {
	
	
	private static List<File> getFiles(FileFolderReader transfo) throws Exception{
		String folderPath = null;
		try{
			folderPath = transfo.getDocument().getStringParser().getValue( transfo.getDocument(), transfo.getFolderPath());
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
		List<File> l = new ArrayList<File>();
		for(String s : f.list()){
			File _f = new File(f, s);
			
			if (!_f.isFile()){
				continue;
			}
			
			if (s.matches(transfo.getFileNamePattern().replace("*", ".*"))){
				l.add(_f);
			}
		}
		
		return l;
	}
	
	public static void createStreamDescriptor(FileFolderReader transfo, int numberRow) throws Exception{
		DefaultStreamDescriptor desc = null;
		
		File firstFile = null;
		
		for(File _f : getFiles(transfo)){
			if (firstFile == null){
				firstFile = _f;
			}
			DefaultStreamDescriptor currdesc = null;
//			if (desc == null){
				try{
					switch(transfo.getFileType()){
					case CSV:
						FileInputCSV t = new FileInputCSV();
						t.setName(transfo.getName());
						t.setDocumentGateway(transfo.getDocument());
						t.setDefinition(_f.getAbsolutePath());
						t.setEncoding(transfo.getEncoding());
						t.setSeparator(transfo.getCsvSeparator());
						FileCSVHelper.createStreamDescriptor(t, numberRow);
						currdesc = (DefaultStreamDescriptor)t.getDescriptor(null);
						break;
//						case VCL:
//							break;
					case XLS:
						FileInputXLS _t = new FileInputXLS();
						_t.setName(transfo.getName());
						_t.setDocumentGateway(transfo.getDocument());
						_t.setDefinition(_f.getAbsolutePath());
						_t.setEncoding(transfo.getEncoding());
						_t.setSheetName(transfo.getXlsSheetName());
						FileXLSHelper.createStreamDescriptor(_t);
						currdesc = (DefaultStreamDescriptor)_t.getDescriptor(null);
						break;
					case XML:
						FileInputXML __t = new FileInputXML();
						__t.setName(transfo.getName());
						__t.setDocumentGateway(transfo.getDocument());
						__t.setDefinition(_f.getAbsolutePath());
						__t.setEncoding(transfo.getEncoding());
						__t.setRootTag(transfo.getXmlRootTag());
						__t.setRowTag(transfo.getXmlRowTag());
						FileXMLHelper.createStreamDescriptor(__t, numberRow);
						currdesc = (DefaultStreamDescriptor)__t.getDescriptor(null);
						break;
					default:
						throw new Exception("Invalid or no File Type defined");
					}
				}catch(Throwable ex){
					throw new Exception("The file " + _f.getName() + " cannot be analyzed, make sure that it correspond to the given type");
				}
				if (desc == null){
					desc = currdesc;
				}
				else{
					//compare the descriptor in case the files are not of the same structures
					if (desc.getColumnCount() != currdesc.getColumnCount()){
						throw new Exception("The file " + _f.getName() + " has not the same number of columns as the file " +  firstFile.getName());
					}
				}
//			}
			
		}
		
		
		if (desc == null){
			throw new Exception("No file matches the given pattern");
		}
		transfo.setDescriptor(desc);
	}

	public static List<List<Object>> getValues(FileFolderReader transfo, int firstRow, int maxRows) throws Exception{
		
		
		List<List<Object>> values = new ArrayList<List<Object>>();
		
		for(File _f : getFiles(transfo)){
			switch(transfo.getFileType()){
			case CSV:
				FileInputCSV t = new FileInputCSV();
				t.setName(transfo.getName());
				t.setDocumentGateway(transfo.getDocument());
				t.setDefinition(_f.getAbsolutePath());
				t.setEncoding(transfo.getEncoding());
				t.setSeparator(transfo.getCsvSeparator());
				
				try{
					FileCSVHelper.createStreamDescriptor(t, 100);
					
					values.addAll(FileCSVHelper.getValues(t, firstRow, maxRows));
				}catch(Exception ex){
					throw new Exception("Error on file " + _f.getAbsolutePath() + ": " + ex.getMessage(), ex);
				}
				
				
				break;
//				case VCL:
//					break;
			case XLS:
				FileInputXLS _t = new FileInputXLS();
				_t.setName(transfo.getName());
				_t.setDocumentGateway(transfo.getDocument());
				_t.setDefinition(_f.getAbsolutePath());
				_t.setEncoding(transfo.getEncoding());
				_t.setSheetName(transfo.getXlsSheetName());
				
				try{
					FileXLSHelper.createStreamDescriptor(_t);
					values.addAll(FileXLSHelper.getValues(_t, firstRow, maxRows));
				}catch(Exception ex){
					throw new Exception("Error on file " + _f.getAbsolutePath() + ": " + ex.getMessage(), ex);
				}
				
				break;
			case XML:
				FileInputXML __t = new FileInputXML();
				__t.setName(transfo.getName());
				__t.setDocumentGateway(transfo.getDocument());
				__t.setDefinition(_f.getAbsolutePath());
				__t.setEncoding(transfo.getEncoding());
				__t.setRootTag(transfo.getXmlRootTag());
				__t.setRowTag(transfo.getXmlRowTag());
				
				try{
					FileXMLHelper.createStreamDescriptor(__t, maxRows - firstRow);
					values.addAll(FileXMLHelper.getValues(__t, firstRow, maxRows));
				}catch(Throwable ex){
					throw new Exception("Error on file " + _f.getAbsolutePath() + ": " + ex.getMessage(), ex);
				}
				
				break;
			default:
				throw new Exception("Invalid or no File Type defined");
			}
		}
		
		return values;
	}
}
