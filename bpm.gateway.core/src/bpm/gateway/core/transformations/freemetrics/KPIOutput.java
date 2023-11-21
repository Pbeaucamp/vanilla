package bpm.gateway.core.transformations.freemetrics;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.gateway.core.AbstractTransformation;
import bpm.gateway.core.DefaultStreamDescriptor;
import bpm.gateway.core.Server;
import bpm.gateway.core.StreamDescriptor;
import bpm.gateway.core.Transformation;
import bpm.gateway.core.exception.ServerException;
import bpm.gateway.core.manager.ResourceManager;
import bpm.gateway.core.server.database.freemetrics.FreemetricServer;
import bpm.gateway.core.transformations.outputs.FreemetricsKPI;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.transformations.kpi.RunKPILoad;
import bpm.vanilla.platform.core.IRepositoryContext;

public class KPIOutput extends AbstractTransformation implements FreemetricsKPI {
	
	private DefaultStreamDescriptor descriptor = new DefaultStreamDescriptor();
	
	public static final String[] DATE_FORMATS = new String[]{
		"yyyy", "yyyy-MM", "yyyy-MM-dd","yyyy-MM-dd hh:mm:ss"
	};
	
	
	/*
	 * the dateFormat for the InputDatas
	 */	
	private String dateFormat = "yyyy-MM-dd";
	/*
	 * flag that indicated if the column is the FmMetricId or FmMetricName
	 */
	private boolean isMetricName;
	
	/*
	 * flag that indicated if the column is the FmApplicationId or FmApplicationName
	 */
	private boolean isApplicationName;
	
	private FreemetricServer server;
	
	private Integer inputValueIndex;
	private Integer inputDateIndex;
	private Integer inputMetricIndex;
	private Integer inputApplicationIndex;
	private Integer inputAssocIndex; 
	private boolean performUpdateOnExistingValues = false;
	
	public boolean isPerformUpdateOnOldValues(){
		return performUpdateOnExistingValues;
	}
	
	public void setPerformUdpateOnOldValues(boolean value){
		this.performUpdateOnExistingValues = value;
	}
	public void setPerformUdpateOnOldValues(String value){
		this.performUpdateOnExistingValues = Boolean.parseBoolean(value);
	}
	
	
	public void setServer(Server server){
		this.server = (FreemetricServer)server;
	}
	
	public void setServer(String server){
		try{
			this.server = (FreemetricServer)ResourceManager.getInstance().getServer(server);	
		}catch(Exception e){
			
		}
		
	}
	
	public Server getServer(){
		return server;
	}
	
	@Override
	public boolean addInput(Transformation stream) throws Exception {
		if (getInputs().size() > 0 && !getInputs().contains(stream)){
			throw new Exception("The Filter Transformations can only have one Input");
		}
		boolean b =  super.addInput(stream);
		
		if (b){
			
			
			refreshDescriptor();
			
		}
		return b;
	}
	
	

	/* (non-Javadoc)
	 * @see bpm.gateway.core.AbstractTransformation#removeInput(bpm.gateway.core.Transformation)
	 */
	@Override
	public void removeInput(Transformation transfo) {
		super.removeInput(transfo);
		inputApplicationIndex = null;
		inputDateIndex = null;
		inputMetricIndex = null;
		inputValueIndex = null;
		
	}

	@Override
	public StreamDescriptor getDescriptor(Transformation transfo) throws ServerException {
		return descriptor;
	}

	@Override
	public Element getElement() {
		Element e = DocumentHelper.createElement("kpiOutput");
		
		
		e.addElement("name").setText(name);
		e.addElement("description").setText(description);
		
		
		e.addElement("positionX").setText("" + x);
		e.addElement("positionY").setText("" + y);
		
	
		
		if (server != null){
			e.addElement("serverRef").setText(server.getName());
		}
		
		
		e.addElement("temporaryFileName").setText(getTemporaryFilename());
		e.addElement("temporarySeparator").setText(getTemporarySpliterChar() + "");

		if (dateFormat != null && !"".equals(dateFormat)){
			e.addElement("dateFormat").setText(dateFormat);
		}
		
		if (inputApplicationIndex != null){
			e.addElement("inputApplicationIndex").setText(inputApplicationIndex + "");
		}
		
		if (inputDateIndex != null){
			e.addElement("inputDateIndex").setText(inputDateIndex + "");
		}
		
		if (inputMetricIndex != null){
			e.addElement("inputMetricIndex").setText(inputMetricIndex + "");
		}
		
		if (inputValueIndex != null){
			e.addElement("inputValueIndex").setText(inputValueIndex + "");
		}
		
		if (inputAssocIndex != null){
			e.addElement("inputAssocIndex").setText(inputAssocIndex + "");
		}
		e.addElement("performUpdateOnExistingValues").setText(performUpdateOnExistingValues + "");
		e.addElement("isApplicationName").setText(isApplicationName + "");
		e.addElement("isMetricName").setText(isMetricName + "");
		
		return e;
	}

//	@Override
//	public TransformationRuntime getExecutioner(RuntimeEngine runtimeEngine) {
//		
//		return new KPILoadRuntime(this, runtimeEngine);
//	}
	
	@Override
	public RuntimeStep getExecutioner(IRepositoryContext repositoryCtx, int bufferSize) {
		return new RunKPILoad(this, bufferSize);
	}

	@Override
	public void refreshDescriptor() {
		if (!isInited()){
			return;
		}

	}

	public Transformation copy() {
		KPIOutput copy = new KPIOutput();
		
		copy.setDescription(description);
		copy.setName("copy of " + name);
		copy.setServer(getServer());

		return copy;

	}

	/**
	 * @return the dateFormat for the DateInputField
	 */
	public final String getDateFormat() {
		return dateFormat;
	}

	/**
	 * @param dateFormat the dateFormat to set
	 */
	public final void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}

	/**
	 * @return the isMetricName : true if the metricField is its name, false if it is its ID
	 */
	public final boolean isMetricName() {
		return isMetricName;
	}

	/**
	 * @param isMetricName the isMetricName to set
	 */
	public final void setMetricName(boolean isMetricName) {
		this.isMetricName = isMetricName;
	}

	/**
	 * @param isMetricName the isMetricName to set
	 */
	public final void setMetricName(String isMetricName) {
		this.isMetricName = Boolean.parseBoolean(isMetricName);
	}

	
	/**
	 * @return the isApplicationName: true if the applicationField is its name, false if it is its ID
	 */
	public final boolean isApplicationName() {
		return isApplicationName;
	}

	/**
	 * @param isApplicationName the isApplicationName to set
	 */
	public final void setApplicationName(boolean isApplicationName) {
		this.isApplicationName = isApplicationName;
	}
	
	/**
	 * @param isApplicationName the isApplicationName to set
	 */
	public final void setApplicationName(String isApplicationName) {
		this.isApplicationName = Boolean.parseBoolean(isApplicationName);
	}

	/**
	 * @return the inputValueIndex
	 */
	public final Integer getInputValueIndex() {
		return inputValueIndex;
	}

	/**
	 * @param inputValueIndex the inputValueIndex to set
	 */
	public final void setInputValueIndex(Integer inputValueIndex) {
		this.inputValueIndex = inputValueIndex;
	}
	public final void setInputValueIndex(String inputValueIndex) {
		this.inputValueIndex = Integer.parseInt(inputValueIndex);
	}

	/**
	 * @return the inputDateIndex
	 */
	public final Integer getInputDateIndex() {
		return inputDateIndex;
	}

	/**
	 * @param inputDateIndex the inputDateIndex to set
	 */
	public final void setInputDateIndex(Integer inputDateIndex) {
		this.inputDateIndex = inputDateIndex;
	}
	public final void setInputDateIndex(String inputDateIndex) {
		this.inputDateIndex = Integer.parseInt(inputDateIndex);
	}

	/**
	 * @return the inputMetricIndex
	 */
	public final Integer getInputMetricIndex() {
		return inputMetricIndex;
	}

	/**
	 * @param inputMetricIndex the inputMetricIndex to set
	 */
	public final void setInputMetricIndex(Integer inputMetricIndex) {
		this.inputMetricIndex = inputMetricIndex;
	}
	public final void setInputMetricIndex(String inputMetricIndex) {
		this.inputMetricIndex = Integer.parseInt(inputMetricIndex);
	}
	

	/**
	 * @return the inputApplicationIndex
	 */
	public final Integer getInputApplicationIndex() {
		return inputApplicationIndex;
	}

	/**
	 * @param inputApplicationIndex the inputApplicationIndex to set
	 */
	public final void setInputApplicationIndex(Integer inputApplicationIndex) {
		this.inputApplicationIndex = inputApplicationIndex;
	}
	
	public final void setInputApplicationIndex(String inputApplicationIndex) {
		this.inputApplicationIndex = Integer.parseInt(inputApplicationIndex);
	}

	public void setInputAssocIndex(String inputAssocIndex) {
		this.inputAssocIndex = Integer.parseInt(inputAssocIndex);
		
	}
	
	public void setInputAssocIndex(Integer inputAssocIndex) {
		this.inputAssocIndex = inputAssocIndex;
		
	}
	
	public Integer getInputAssocIndex(){
		return this.inputAssocIndex ;
	}
	public String getAutoDocumentationDetails() {
		StringBuffer buf = new StringBuffer();
		buf.append("DateFormat : " + dateFormat + "\n");
		buf.append("Application is Id : " + isApplicationName + "\n");
		try{
			buf.append("Application Field : " + getInputs().get(0).getDescriptor(this).getColumnName(inputApplicationIndex) +"\n");
		}catch(Exception ex){
			buf.append("Application Field : "  + inputApplicationIndex + "\n");
		}
		
		buf.append("Metric is Id : " + isMetricName+ "\n");
		try{
			buf.append("Metric Field : " + getInputs().get(0).getDescriptor(this).getColumnName(inputMetricIndex) + "\n");
		}catch(Exception ex){
			buf.append("Metric Field : "  + inputMetricIndex + "\n");
		}
		
		try{
			buf.append("Association Field : " + getInputs().get(0).getDescriptor(this).getColumnName(inputAssocIndex) + "\n");
		}catch(Exception ex){
			buf.append("Association Field : "  + inputAssocIndex + "\n");
		}
		
		try{
			buf.append("Value Field : " + getInputs().get(0).getDescriptor(this).getColumnName(inputValueIndex) + "\n");
		}catch(Exception ex){
			buf.append("Value Field : "  + inputValueIndex + "\n");
		}
		
		
		try{
			buf.append("Date Field : " + getInputs().get(0).getDescriptor(this).getColumnName(inputDateIndex) + "\n");
		}catch(Exception ex){
			buf.append("DateField : "  + inputDateIndex + "\n");
		}
		buf.append("FreeMetrics Server : "+ getServer().getName() + "\n");		
		buf.append("Perform Update On existing Values : "+ performUpdateOnExistingValues + "\n");
		return buf.toString();
	}
	
}
