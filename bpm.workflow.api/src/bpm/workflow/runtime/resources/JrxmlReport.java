package bpm.workflow.runtime.resources;

import org.dom4j.Element;

/**
 * Object of a Jasper Report
 * @author CHARBONNIER, MARTIN
 *
 */
public class JrxmlReport extends ReportObject {
	private Integer dataSourceId; 

	
	public JrxmlReport() {
		super();
	}
	
	/**
	 * Create an object which is a Jasper Report
	 * @param id of the directory item
	 * @param repository Server
	 */
	public JrxmlReport(int id) {
		super(id);	
	}
	
	@Override
	public int getType() {
		return JASPER;
	}

	@Override
	protected void setParameterNames() {
		

	}
	
	@Override
	public Element getXmlNode() {
		Element e = super.getXmlNode();
		if(dataSourceId != null){
		e.addElement("datasourceid").setText(dataSourceId+"");
		}
		e.setName("jrxmlObject");
		return e;
	}

	/**
	 * 
	 * @return the id relative to the datasource of the Jasper Report
	 */
	public int getDataSourceId() {
		return dataSourceId;
	}

	/**
	 * Set the id relative to the datasource of the Jasper Report
	 * @param dataSourceId
	 */
	public void setDataSourceId(int dataSourceId) {
		this.dataSourceId = dataSourceId;
	}
	
	/**
	 * Set the id relative to the datasource of the Jasper Report
	 * @param dataSourceId
	 */
	public void setDataSourceId(String dataSourceId) {
		this.dataSourceId = new Integer(dataSourceId);
	}

}
