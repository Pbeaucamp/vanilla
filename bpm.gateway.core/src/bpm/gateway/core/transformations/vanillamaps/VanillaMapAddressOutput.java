package bpm.gateway.core.transformations.vanillamaps;

import org.dom4j.Element;

import bpm.gateway.core.Transformation;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.transformation.norparena.NorparenaInsertAdresses;
import bpm.vanilla.platform.core.IRepositoryContext;



public class VanillaMapAddressOutput extends VanillaMapAddressInput{
	private Integer inputArrondissementIndex;
	private Integer inputBlocIndex;
	private Integer inputCityIndex;
	private Integer inputCountryIndex;
	private Integer inputIdIndex;
	private Integer inputInseeCodeIndex;
	private Integer inputLabelIndex;
	private Integer inputStreet1Index;
	private Integer inputStreet2Index;
	private Integer inputZipcodeIndex;
	
	/**
	 * @return the inputArrondissementIndex
	 */
	public Integer getInputArrondissementIndex() {
		return inputArrondissementIndex;
	}
	/**
	 * @param inputArrondissementIndex the inputArrondissementIndex to set
	 */
	public void setInputArrondissementIndex(Integer inputArrondissementIndex) {
		this.inputArrondissementIndex = inputArrondissementIndex;
	}
	public void setInputArrondissementIndex(String inputArrondissementIndex) {
		this.inputArrondissementIndex = Integer.parseInt(inputArrondissementIndex);
	}
	/**
	 * @return the inputBlocIndex
	 */
	public Integer getInputBlocIndex() {
		return inputBlocIndex;
	}
	/**
	 * @param inputBlocIndex the inputBlocIndex to set
	 */
	public void setInputBlocIndex(Integer inputBlocIndex) {
		this.inputBlocIndex = inputBlocIndex;
	}
	public void setInputBlocIndex(String inputBlocIndex) {
		this.inputBlocIndex = Integer.parseInt(inputBlocIndex);
	}
	/**
	 * @return the inputCityIndex
	 */
	public Integer getInputCityIndex() {
		return inputCityIndex;
	}
	/**
	 * @param inputCityIndex the inputCityIndex to set
	 */
	public void setInputCityIndex(Integer inputCityIndex) {
		this.inputCityIndex = inputCityIndex;
	}
	public void setInputCityIndex(String inputCityIndex) {
		this.inputCityIndex = Integer.parseInt(inputCityIndex);
	}
	/**
	 * @return the inputCountryIndex
	 */
	public Integer getInputCountryIndex() {
		return inputCountryIndex;
	}
	/**
	 * @param inputCountryIndex the inputCountryIndex to set
	 */
	public void setInputCountryIndex(Integer inputCountryIndex) {
		this.inputCountryIndex = inputCountryIndex;
	}
	public void setInputCountryIndex(String inputCountryIndex) {
		this.inputCountryIndex = Integer.parseInt(inputCountryIndex);
	}
	/**
	 * @return the inputIdIndex
	 */
	public Integer getInputIdIndex() {
		return inputIdIndex;
	}
	/**
	 * @param inputIdIndex the inputIdIndex to set
	 */
	public void setInputIdIndex(Integer inputIdIndex) {
		this.inputIdIndex = inputIdIndex;
	}
	public void setInputIdIndex(String inputIdIndex) {
		this.inputIdIndex = Integer.parseInt(inputIdIndex);
	}
	/**
	 * @return the inputInseeCodeIndex
	 */
	public Integer getInputInseeCodeIndex() {
		return inputInseeCodeIndex;
	}
	/**
	 * @param inputInseeCodeIndex the inputInseeCodeIndex to set
	 */
	public void setInputInseeCodeIndex(Integer inputInseeCodeIndex) {
		this.inputInseeCodeIndex = inputInseeCodeIndex;
	}
	public void setInputInseeCodeIndex(String inputInseeCodeIndex) {
		this.inputInseeCodeIndex = Integer.parseInt(inputInseeCodeIndex);
	}
	/**
	 * @return the inputLabelIndex
	 */
	public Integer getInputLabelIndex() {
		return inputLabelIndex;
	}
	/**
	 * @param inputLabelIndex the inputLabelIndex to set
	 */
	public void setInputLabelIndex(Integer inputLabelIndex) {
		this.inputLabelIndex = inputLabelIndex;
	}
	public void setInputLabelIndex(String inputLabelIndex) {
		this.inputLabelIndex = Integer.parseInt(inputLabelIndex);
	}
	/**
	 * @return the inputStreet1Index
	 */
	public Integer getInputStreet1Index() {
		return inputStreet1Index;
	}
	/**
	 * @param inputStreet1Index the inputStreet1Index to set
	 */
	public void setInputStreet1Index(Integer inputStreet1Index) {
		this.inputStreet1Index = inputStreet1Index;
	}
	public void setInputStreet1Index(String inputStreet1Index) {
		this.inputStreet1Index = Integer.parseInt(inputStreet1Index);
	}
	/**
	 * @return the inputStreet2Index
	 */
	public Integer getInputStreet2Index() {
		return inputStreet2Index;
	}
	/**
	 * @param inputStreet2Index the inputStreet2Index to set
	 */
	public void setInputStreet2Index(Integer inputStreet2Index) {
		this.inputStreet2Index = inputStreet2Index;
	}
	public void setInputStreet2Index(String inputStreet2Index) {
		this.inputStreet2Index = Integer.parseInt(inputStreet2Index);
	}
	/**
	 * @return the inputZipcodeIndex
	 */
	public Integer getInputZipcodeIndex() {
		return inputZipcodeIndex;
	}
	/**
	 * @param inputZipcodeIndex the inputZipcodeIndex to set
	 */
	public void setInputZipcodeIndex(Integer inputZipcodeIndex) {
		this.inputZipcodeIndex = inputZipcodeIndex;
	}
	public void setInputZipcodeIndex(String inputZipcodeIndex) {
		this.inputZipcodeIndex = Integer.parseInt(inputZipcodeIndex);
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
	
	@Override
	public RuntimeStep getExecutioner(IRepositoryContext repositoryCtx, int bufferSize) {
		try {
			return new NorparenaInsertAdresses(repositoryCtx, this, bufferSize);
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}
	
	@Override
	public Element getElement() {
		Element e =  super.getElement();
		
		
		e.setName("vanillaMapAddressOutput");
		if (getInputArrondissementIndex() != null){
			e.addElement("arrondissementIndex").setText(getInputArrondissementIndex()+ "");
		}
		if (getInputBlocIndex() != null){
			e.addElement("blocIndex").setText(getInputBlocIndex()+ "");
		}
		if (getInputCityIndex() != null){
			e.addElement("cityIndex").setText(getInputCityIndex()+ "");
		}
		if (getInputCountryIndex() != null){
			e.addElement("countryIndex").setText(getInputCountryIndex()+ "");
		}
		if (getInputIdIndex() != null){
			e.addElement("idIndex").setText(getInputIdIndex()+ "");
		}
		if (getInputLabelIndex() != null){
			e.addElement("labelIndex").setText(getInputLabelIndex()+ "");
		}
		if (getInputInseeCodeIndex() != null){
			e.addElement("inseeCodeIndex").setText(getInputInseeCodeIndex()+ "");
		}
		if (getInputStreet1Index() != null){
			e.addElement("street1Index").setText(getInputStreet1Index()+ "");
		}
		if (getInputStreet2Index() != null){
			e.addElement("street2Index").setText(getInputStreet2Index()+ "");
		}
		if (getInputZipcodeIndex() != null){
			e.addElement("zipCodeIndex").setText(getInputZipcodeIndex() + "");
		}
		
		
		return e;
	}
	
	
}
