package bpm.gateway.runtime2.transformation.selection;

import java.util.ArrayList;
import java.util.List;

import bpm.gateway.core.Transformation;
import bpm.gateway.core.transformations.cleansing.ValidationCleansing;
import bpm.gateway.core.transformations.cleansing.ValidatorHelper;
import bpm.gateway.core.transformations.cleansing.ValidationCleansing.ValidationOutput;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.internal.Row;

public class RunValidationCleansing extends RuntimeStep{

	
	private List<RuntimeStep> errorsSteps = new ArrayList<RuntimeStep>();
	private List<List<ValidationOutput>> validations = new ArrayList<List<ValidationOutput>>();
	private RuntimeStep trashOutput;
	
	public RunValidationCleansing(ValidationCleansing transformation, int bufferSize) {
		super(null, transformation, bufferSize);
	}

	@Override
	public void init(Object adapter) throws Exception {
		ValidationCleansing tr = (ValidationCleansing)getTransformation();
		validations.addAll(tr.getValidators());
		for(int i = 0; i < tr.getDescriptor(tr).getColumnCount(); i++){
			for(ValidationOutput vo : tr.getValidators(i)){
				
				
				for(RuntimeStep rs : getOutputs()){
					if (rs.getTransformation() == vo.getOutput()  && !errorsSteps.contains(rs)){
						errorsSteps.add(rs);
						break;
					}
				}
				
			}
			if (tr.getTrashTransformation() != null && !errorsSteps.contains(tr.getTrashTransformation())){
				
				for(RuntimeStep rs : getOutputs()){
					if (rs.getTransformation() == tr.getTrashTransformation() && ! errorsSteps.contains(rs)){
						trashOutput = rs;
						break;
					}
				}
				
			}
		}
		
		
		info(" inited");
		
		
	}

	
	private RuntimeStep getErrorOutput(Transformation output){
		for(RuntimeStep o : errorsSteps){
			if (o.getTransformation() == output){
				return o;
			}
		}
		return null;
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
			try{
				Thread.sleep(10);
				return;
			}catch(InterruptedException e){
				
			}
		}
		
		Row row = readRow();
		
		try{
			RuntimeStep  checkoutErrrosSteps = checkRow(row);
			
//			if (checkoutErrrosSteps == null){
			writeRow(checkoutErrrosSteps, row);
//			}
		}catch(Exception ex){
			if (trashOutput != null){
				writeRow(trashOutput, row);
			}
			else{
				error("Error on row " + row.dump() + " : " + ex.getMessage(), ex);
			}
		}
		
	
		
		
	}
	
	
	protected RuntimeStep checkRow(Row row) throws Exception{
		
		for(int i = 0; i < row.getMeta().getSize(); i++){
			boolean validated = true;
			
			for(ValidationOutput v : ((ValidationCleansing)getTransformation()).getValidators(i)){
				if (v.getValidator() == null){
					continue;
				}
				
				if (row.get(i) == null){
					validated = false;
				}
				else{
					validated = validated && ValidatorHelper.isValid(row.get(i).toString(), v.getValidator().getRegex(), v.getValidator().getType());
				}
				
				if (validated == false){
				
					RuntimeStep rs = getErrorOutput(v.getOutput());
					
					if (rs == null){
						if (trashOutput != null){
							return trashOutput;
						}
						else{
							throw new Exception("");
						}
					}
					else{
						return rs;
					}
				}
				
			}
			
			
			
		}
		
		return null;
	}

	@Override
	public void releaseResources() {
		errorsSteps = null;
		trashOutput = null;
		validations = null;
		info( " resources released");
		
	}
	
	/**
	 * write the given data in the all the outputs
	 * @param data
	 * @throws InterruptedException
	 */
	protected void writeRow(RuntimeStep  runtimeStep, Row row) throws InterruptedException{
		
		if (runtimeStep != null){
			runtimeStep.insertRow(row, this);
		}
		else{
			for(RuntimeStep r : getOutputs()){
				if (!errorsSteps.contains(r)){
					r.insertRow(row, this);
				}
			}
		}
		
		writedRows++;
	}
	
	@Override
	protected void writeRow(Row row) throws InterruptedException {
		//DO NOTHING 
	}

}
