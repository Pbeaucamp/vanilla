package bpm.gateway.core.transformations.files;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.gateway.core.AbstractTransformation;
import bpm.gateway.core.DefaultStreamDescriptor;
import bpm.gateway.core.StreamDescriptor;
import bpm.gateway.core.Transformation;
import bpm.gateway.core.exception.ServerException;
import bpm.gateway.core.server.file.FileCSVHelper;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.transformations.inputs.folder.RunFileFolderReader;
import bpm.vanilla.platform.core.IRepositoryContext;

/**
 * 
 * @author ludo
 *
 */
public class FileFolderReader extends AbstractTransformation{
	
	public static enum FileType{
		CSV, XLS, XML;
	}
	
	

	private FileType fileType = FileType.CSV;
	
	private String folderPath;
	private String fileNamePattern = "*";
	private String encoding = "UTF-8";
	
	private String csvSeparator = ";";
	
	/**
	 * only for XLS,CSV,VCL
	 */
	private boolean skipFirstRow = true;
	
	private String xlsSheetName;
	private String xmlRowTag = "Row";
	private String xmlRootTag = "Root";
	
	private StreamDescriptor descriptor = new DefaultStreamDescriptor();

	/**
	 * @return the fileType
	 */
	public FileType getFileType() {
		return fileType;
	}
	/**
	 * @param fileType the fileType to set
	 */
	public void setFileType(FileType fileType) {
		this.fileType = fileType;
	}
	
	public void setFileType(String fileType) {
		try{
			this.fileType = FileType.valueOf(fileType);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	/**
	 * @return the folderPath
	 */
	public String getFolderPath() {
		return folderPath;
	}
	/**
	 * @param folderPath the folderPath to set
	 */
	public void setFolderPath(String folderPath) {
		this.folderPath = folderPath;
	}
	/**
	 * @return the fileNamePattern
	 */
	public String getFileNamePattern() {
		return fileNamePattern;
	}
	/**
	 * @param fileNamePattern the fileNamePattern to set
	 */
	public void setFileNamePattern(String fileNamePattern) {
		this.fileNamePattern = fileNamePattern;
	}
	@Override
	public boolean addInput(Transformation stream) throws Exception {
		if (getInputs().size() > 0){
			throw new Exception("Can only have one Input");
		}
		return super.addInput(stream);
	}
	@Override
	public StreamDescriptor getDescriptor(Transformation transfo) throws ServerException {
		return descriptor;
	}

	@Override
	public Element getElement() {
		Element e = DocumentHelper.createElement("fileFolderReader");
		e.addElement("name").setText(name);
		e.addElement("description").setText(description);
		
		
		e.addElement("positionX").setText("" + x);
		e.addElement("positionY").setText("" + y);
		
		
		
		
		e.addElement("temporaryFileName").setText(getTemporaryFilename());
		e.addElement("temporarySeparator").setText(getTemporarySpliterChar() + "");
		e.addElement("encoding").setText(getEncoding());
		
		
		if (getCsvSeparator() != null){
			e.addElement("separator").setText(getCsvSeparator() + "");
		}
		e.addElement("skipFirstRow").setText(skipFirstRow+"");
		
		if (getEncoding() != null){
			e.addElement("encoding").setText(getEncoding() + "");
		}
		
		if (getFileNamePattern() != null){
			e.addElement("fileNamePattern").setText(getFileNamePattern() + "");
		}
		
		if (getFolderPath() != null){
			e.addElement("folderPath").setText(getFolderPath() + "");
		}
		
		if (getFileType() != null){
			e.addElement("fileType").setText(getFileType() + "");
		}
		
		if (getXlsSheetName() != null){
			e.addElement("xlsSheetName").setText(getXlsSheetName() + "");
		}
		
		if (getXmlRootTag() != null){
			e.addElement("xmlRootTag").setText(getXmlRootTag() + "");
		}
		
		if (getXmlRowTag() != null){
			e.addElement("xmlRowTag").setText(getXmlRowTag() + "");
		}
		
		
		if (descriptor != null){
			e.add(descriptor.getElement());
		}
		
		return e;

	}

	@Override
	public RuntimeStep getExecutioner(IRepositoryContext repositoryCtx, int bufferSize) {
		return new RunFileFolderReader(this, bufferSize);
	}

	@Override
	public void refreshDescriptor() {
		if (!isInited()){
			return;
		}
		
	}

	public Transformation copy() {
		
		return null;
	}

	public String getAutoDocumentationDetails() {
		
		return null;
	}
	
	protected void setDescriptor(DefaultStreamDescriptor desc){
		
		this.descriptor = desc;
		refreshDescriptor();
		this.fireChangedProperty();
	}

	
	public String getEncoding(){
		return encoding;
	}
	
	public void setEncoding(String encoding){
		this.encoding = encoding;
	}
	/**
	 * @return the csvSeparator
	 */
	public String getCsvSeparator() {
		return csvSeparator;
	}
	/**
	 * @param csvSeparator the csvSeparator to set
	 */
	public void setCsvSeparator(String csvSeparator) {
		this.csvSeparator = csvSeparator;
	}
	/**
	 * @return the xlsSheetName
	 */
	public String getXlsSheetName() {
		return xlsSheetName;
	}
	/**
	 * @param xlsSheetName the xlsSheetName to set
	 */
	public void setXlsSheetName(String xlsSheetName) {
		this.xlsSheetName = xlsSheetName;
	}
	/**
	 * @return the xmlRowTag
	 */
	public String getXmlRowTag() {
		return xmlRowTag;
	}
	/**
	 * @param xmlRowTag the xmlRowTag to set
	 */
	public void setXmlRowTag(String xmlRowTag) {
		this.xmlRowTag = xmlRowTag;
	}
	/**
	 * @return the xmlRootTag
	 */
	public String getXmlRootTag() {
		return xmlRootTag;
	}
	/**
	 * @param xmlRootTag the xmlRootTag to set
	 */
	public void setXmlRootTag(String xmlRootTag) {
		this.xmlRootTag = xmlRootTag;
	}
	/**
	 * @return the skipFirstRow
	 */
	public boolean isSkipFirstRow() {
		return skipFirstRow;
	}
	/**
	 * @param skipFirstRow the skipFirstRow to set
	 */
	public void setSkipFirstRow(boolean skipFirstRow) {
		this.skipFirstRow = skipFirstRow;
	}
	
	/**
	 * do not use, just for Digester
	 * @param skipFirstRow
	 */
	public void setSkipFirstRow(String skipFirstRow) {
		try{
			this.skipFirstRow = Boolean.parseBoolean(skipFirstRow);
		}catch(Exception ex){
			
		}
	}
	
	public void initDescriptor(){
		try{
			FileFolderHelper.createStreamDescriptor(this, 100);
		
		}catch(Exception e){
			e.printStackTrace();
		}
		super.initDescriptor();
	}
}
