package bpm.workflow.runtime.resources;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Element;

/**
 * Report object from the repository
 * @author CAMUS, CHARBONNIER, MARTIN
 *
 */
public abstract class ReportObject extends BiRepositoryObject {

	public static final int FWR = 0;
	public static final int BIRT = 1;
	public static final int JASPER = 2;
	
	
	public static final int PDF = 0;
	public static final int HTML = 1;
	public static final int XLS = 2;
	
	public static final String[] OUTPUT_FORMATS = new String[]{"HTML", "PDF", "XLS", "XLSX", "ODS", "DOC", "DOCX", "ODT", "PPT", "PPTX", "POSTSCRIPT"};
	
	private String outputName = "";
	private int outputType = -1;
	
	private List<Integer> outputFormats = new ArrayList<Integer>();
	
//	private int outputFormat = 0;
	
	/**
	 * do not use, only for parsing XML
	 */
	public ReportObject(){super();}
	
	
	/**
	 * Create a report object
	 * @param id of the directory item
	 * @param repository Server
	 */
	public ReportObject(int id) {
		super(id);
		
	}

	
	
	/**
	 * 
	 * @return the type (Jasper : 2, FWR : 0 or BIRT : 1)
	 */
	public abstract int getType();
	
	
	/**
	 * Set the output type (Jasper : 2, FWR : 0 or BIRT : 1)
	 * @param outputType
	 */
	public void setOutputType(int outputType){
		this.outputType = outputType;
	}
	
	/**
	 * do not use, only for parsing XML
	 */
	public void setOutputType(String outputType){
		try{
			this.outputType = Integer.parseInt(outputType);
		}catch(NumberFormatException e){
			
		}
		
	}
	
	/**
	 * 
	 * @return the output type of the report (Jasper : 2, FWR : 0 or BIRT : 1)
	 */
	public int getOutputType(){
		return outputType;
	}


	/**
	 * 
	 * @return the output name of the report
	 */
	public String getOutputName() {
		return outputName;
	}


	/**
	 * do not use, only for parsing XML
	 */
	public void setOutputName(String outputName) {
		this.outputName = outputName;
	}



	@Override
	public Element getXmlNode() {
		Element e = super.getXmlNode();
		e.setName("reportObject");
		if(outputType != -1){
			e.addElement("outputType").setText(outputType + "");
		}
		if(!outputName.equalsIgnoreCase("")){
			e.addElement("outputName").setText(outputName);
		}
		for(int i : outputFormats) {
			e.addElement("outputFormat").setText(OUTPUT_FORMATS[i] + "");
		}
		
		return e;
	}


	/**
	 * Set the format of the output
	 * @param i (pdf = 0, html =1, xls = 2)
	 */
	public void setOutputFormats(List<Integer> formats) {
		outputFormats = formats;
		
	}
	
	/**
	 * Set the format of the output
	 * @param i ("pdf", "html", "xls")
	 */
	public void setOutputFormat(String i) {
		try{
			if(outputFormats == null) {
				outputFormats = new ArrayList<Integer>();
			}
			int index = 0;
			for(int ii = 0 ; ii < OUTPUT_FORMATS.length ; ii++) {
				if(OUTPUT_FORMATS[ii].equals(i)) {
					index = ii;
					break;
				}
			}
			outputFormats.add(index);
		}catch(NumberFormatException e){
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 
	 * @return the format of the output (pdf = 0, html =1, xls = 2)
	 */
	public List<Integer> getOutputFormats(){
		return outputFormats;
	}
	@Override
	protected void setParameterNames() {
		
		
	}


	public void removeParameter(String string) {
		if (parameters != null && parameters.size() > 0) {
			
			parameters.remove(string);
		}
	}

}
