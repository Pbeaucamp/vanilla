package bpm.gateway.core.transformations.cleansing;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.gateway.core.AbstractTransformation;
import bpm.gateway.core.DefaultStreamDescriptor;
import bpm.gateway.core.StreamDescriptor;
import bpm.gateway.core.StreamElement;
import bpm.gateway.core.Transformation;
import bpm.gateway.core.Trashable;
import bpm.gateway.core.exception.ServerException;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.transformation.selection.RunValidationCleansing;
import bpm.vanilla.platform.core.IRepositoryContext;

/**
 * each fields of the input stream is attached to ValidationOuput (or a null one)
 * each regex within the ValidationOutput will be applied to each values
 * if on is not satisfied, the row will be sent in the ValidationOutput step name if it exists
 * or in the trash output if it doesnt exists or will be lost in the nature
 * @author ludo
 *
 */
public class ValidationCleansing extends AbstractTransformation implements Trashable{

	protected DefaultStreamDescriptor descriptor = new DefaultStreamDescriptor();
	
	protected Transformation trashStep;
	protected String trashName;
	
	
	
	
	
	public static class Validator{

		
		private String regex;
		private ValidatorType type = ValidatorType.Regex;
		private String name;
		public Validator(){}
		public Validator(String regex, String name, ValidatorType type){
			this.regex = regex;
			if (type != null){
				this.type = type;
			}
			this.name = name;
		}
		public String getRegex(){
			return regex;
		}
		public ValidatorType getType(){
			return type;
		}
		public String getName(){
			return name;
		}
		public void setName(String name){
			this.name = name;
		}
		
		public void setRegex(String regex){
			this.regex = regex;
		}
		
		public void setType(String type){
			try{
				this.type = ValidatorType.valueOf(type);
			}catch(Exception ex){
				
			}
		}
		
		public Element getElement(){
			Element e = DocumentHelper.createElement("validator");
			if (getType() != null){
				e.addElement("type").setText(type.name());
			}
			if (getName() != null){
				e.addElement("name").setText(getName());
			}
			if (getRegex() != null){
				e.addElement("pattern").setText(getRegex());
			}
			
			return e;
		}
		
	}
	
	public static enum ValidatorType{
		Date, Decimal, Regex;
	}

	
	public static class ValidationOutput {
	
		private Validator validator;
		private String outputName;
		private Transformation output;
		private ValidationCleansing owner;
		
		public Transformation getOutput(){
			if (outputName != null){
				output = owner.getDocument().getTransformation(outputName);
				outputName = null;
				
			}
			return output;
			
		}
		
		public void setOutput(Transformation t){
			this.output = t;
		}
		
		
		private String comment = "new validator";
		
		/**
		 * @return the comment
		 */
		public String getComment() {
			return comment;
		}
		/**
		 * @param comment the comment to set
		 */
		public void setComment(String comment) {
			this.comment = comment;
		}
		/**
		 * @return the regex
		 */
		public Validator getValidator() {
			return validator;
		}
		/**
		 * @param regex the regex to set
		 */
		public void setValidator(Validator validator) {
			this.validator = validator;
		}
		
		
		
		/**
		 * @param outputName the outputName to set
		 */
		public void setOutputName(String outputName) {
			this.outputName = outputName;
		}
		public Element getElement() {
			Element e = DocumentHelper.createElement("validationOutput");
			e.addElement("description").setText(getComment());
			if (getOutput() != null){
				e.addElement("outputStepName").setText(getOutput().getName());
			}
			if (validator != null){
				e.add(validator.getElement());
			}
			
			return e;
		}

		public void setOwner(ValidationCleansing validationCleansing) {
			this.owner = validationCleansing;
			
		}
	}
	
	/**
	 * the position inside the list == position of field from the incoming stream
	 */
	private List<List<ValidationOutput>> validators = new ArrayList<List<ValidationOutput>>();
	
	
	
	public List<List<ValidationOutput>>  getValidators(){
		return new ArrayList<List<ValidationOutput>>(validators);
	}
	
	
	public void addValidator(ValidationOutput outputConfig, int streamElementPosition){
		while(validators.size() <= streamElementPosition){
			validators.add(new ArrayList<ValidationOutput>());
			
		}
		
		validators.get(streamElementPosition).add(outputConfig);
		outputConfig.setOwner(this);
	}
	
	public void setFieldValidators(String positions, Object validators){
		for(Object v : (List)validators){
			addValidator((ValidationOutput)v, Integer.parseInt(positions));
		}
	}
	
	public void removeValidator(ValidationOutput v){
		for(List<ValidationOutput> l : validators){
			l.remove(v);
		}
	}
	
	public List<ValidationOutput> getValidators(int fieldPosition){
		if (fieldPosition < validators.size()){
			return validators.get(fieldPosition);
		}
		
		return new ArrayList<ValidationOutput>();
	}
	
	@Override
	public boolean addInput(Transformation t)throws Exception{
		if (inputs.size() >= 1 && !inputs.contains(t)){
			throw new Exception("Cannot have more than 1 Stream input");
		}
		
		boolean b =  super.addInput(t);
		if (b){
			refreshDescriptor();
		}
		return b;
	}
	
	@Override
	public StreamDescriptor getDescriptor(Transformation transfo) throws ServerException {
		return descriptor;
	}

	@Override
	public Element getElement() {
		Element e = DocumentHelper.createElement("validationCleansing");
		e.addElement("name").setText(name);
		e.addElement("description").setText(description);
		
		
		e.addElement("positionX").setText("" + x);
		e.addElement("positionY").setText("" + y);

		e.addElement("temporaryFileName").setText(getTemporaryFilename());
		e.addElement("temporarySeparator").setText(getTemporarySpliterChar() + "");
		

		if (getTrashTransformation() != null){
			e.addElement("trashOutput").setText(getTrashTransformation().getName());
		}
		
		
		for(int i = 0; i < descriptor.getColumnCount(); i++){
			Element fE = e.addElement("fieldValidators");
			fE.addElement("fieldIndex").setText(i + "");
			Element listeValidator = fE.addElement("validationOutputs");
			for(ValidationOutput v : getValidators(i)){
				listeValidator.add(v.getElement());
			}
			
		}
		
		
		return e;
	}

	@Override
	public RuntimeStep getExecutioner(IRepositoryContext repositoryCtx, int bufferSize) {
		return new RunValidationCleansing(this, bufferSize);
	}

	@Override
	public void refreshDescriptor() {
		if (!isInited()){
			return;
		}
		try{
			descriptor = new DefaultStreamDescriptor();
			for(Transformation t : getInputs()){
				for(StreamElement e : t.getDescriptor(this).getStreamElements()){
					descriptor.addColumn(e.clone(getName(), t.getName()));
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		for(Transformation  t : getOutputs()){
			t.refreshDescriptor();
		}

		
	}

	public Transformation copy() {
		return null;
	}

	public String getAutoDocumentationDetails() {
		StringBuffer buf = new StringBuffer();
		return buf.toString();
	}


	public Transformation getTrashTransformation() {
		if (trashName != null && getDocument() != null){
			trashStep = getDocument().getTransformation(trashName);
			trashName = null;
		}
		return trashStep;
	}


	public void setTrashTransformation(Transformation transfo) {
		this.trashStep = transfo;
		
	}
	
	public void setTrashTransformation(String name){
		this.trashName = name;
	}


	public StreamElement getStreamElementFor(ValidationOutput element) {
		
		
		try{
			for(int i = 0; i < validators.size(); i++){
				if (validators.get(i).contains(element)){
					return descriptor.getStreamElements().get(i);
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
}
