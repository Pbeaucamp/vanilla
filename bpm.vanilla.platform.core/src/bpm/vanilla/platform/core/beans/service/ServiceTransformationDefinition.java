package bpm.vanilla.platform.core.beans.service;

import java.util.ArrayList;
import java.util.List;

/**
 * This represents what to do with the data received by the vanillaWebService
 * 
 * It need input(s) and output definitions
 * and also fail conditions/events.
 * 
 * @author Marc Lanquetin
 *
 */
public class ServiceTransformationDefinition {

	private Integer id;
	private String name;
	
	private String xmlRoot;
	private String xmlRow;
	
	private List<ServiceInputData> inputs;
	private List<ServiceOutputData> outputs;
	private List<ServiceReturnData> returns;

	public List<ServiceInputData> getInputs() {
		return inputs;
	}

	public void setInputs(List<ServiceInputData> inputs) {
		this.inputs = new ArrayList<ServiceInputData>(inputs);
	}

	public List<ServiceOutputData> getOutputs() {
		return outputs;
	}

	public void setOutputs(List<ServiceOutputData> outputs) {
		this.outputs = new ArrayList<ServiceOutputData>(outputs);
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getId() {
		return id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public String getXmlRoot() {
		return xmlRoot;
	}

	public void setXmlRoot(String xmlRoot) {
		this.xmlRoot = xmlRoot;
	}

	public String getXmlRow() {
		return xmlRow;
	}

	public void setXmlRow(String xmlRow) {
		this.xmlRow = xmlRow;
	}

	public void setReturns(List<ServiceReturnData> returns) {
		if(returns != null){
			this.returns = new ArrayList<ServiceReturnData>(returns);
		}
	}

	public List<ServiceReturnData> getReturns() {
		return returns;
	}
}
