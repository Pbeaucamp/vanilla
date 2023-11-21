package bpm.gateway.core.transformations;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.gateway.core.AbstractTransformation;
import bpm.gateway.core.DefaultStreamDescriptor;
import bpm.gateway.core.StreamDescriptor;
import bpm.gateway.core.Transformation;
import bpm.gateway.core.exception.ServerException;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.transformation.normalisation.RunNormalize;
import bpm.vanilla.platform.core.IRepositoryContext;

public class Normalize extends AbstractTransformation{

	private DefaultStreamDescriptor descriptor = new DefaultStreamDescriptor();
	
	private List<Integer> inputLevelIndex = new ArrayList<Integer>(); 
	
	
	@Override
	public StreamDescriptor getDescriptor(Transformation transfo) throws ServerException {
		return descriptor;
	}

	public void setInputLevelIndex(int levelNumber, Integer inputIndex){
		
		while(inputLevelIndex.size() <= levelNumber){
			inputLevelIndex.add(-1);
		}
		inputLevelIndex.set(levelNumber, inputIndex);
		refreshDescriptor();
	}
	
	
	public void setInputLevelIndex(String levelNumber, String inputIndex){
		try{
			int pos = Integer.parseInt(levelNumber);
			int val = Integer.parseInt(inputIndex);
			
			for(int i = inputLevelIndex.size(); i <= pos; i++){
				inputLevelIndex.add(-1);
			}
			inputLevelIndex.set(pos, val);
		}catch(Exception ex){
			
		}
	}
	
	public void addLevel(){
		inputLevelIndex.add( -1);
	}
	
	public void removeLevel(int pos){
		inputLevelIndex.remove(pos);
	}
	
	public int getInputIndexForLevel(int lvlNumber){
		return inputLevelIndex.get(lvlNumber);
	}
	
	public List<Integer> getLevelsIndex(){
		return inputLevelIndex;
	}
	
	@Override
	public boolean addInput(Transformation t)throws Exception{
		if (inputs.size() != 0 && !inputs.contains(t)){
			throw new Exception("Cannot have more tha one Stream input");
		}
		boolean b =  super.addInput(t);
		if (b){
			refreshDescriptor();
		}
		return b;
	}
	
	@Override
	public Element getElement() {
		Element e = DocumentHelper.createElement("normalize");
		e.addElement("name").setText(name);
		e.addElement("description").setText(description);
		
		
		e.addElement("positionX").setText("" + x);
		e.addElement("positionY").setText("" + y);
		e.addElement("temporaryFileName").setText(getTemporaryFilename());
		e.addElement("temporarySeparator").setText(getTemporarySpliterChar() + "");
		
		for(int i = 0; i < inputLevelIndex.size(); i++){
			Element _l = e.addElement("level");
			_l.addElement("number").setText("" + i);
			_l.addElement("inputFieldIndex").setText("" + inputLevelIndex.get(i));
		}
		
		return e;
	}

	@Override
	public RuntimeStep getExecutioner(IRepositoryContext repositoryCtx, int bufferSize) {
		return new RunNormalize(this, bufferSize);
	}

	@Override
	public void refreshDescriptor() {
		if (!isInited()){
			return;
		}
		
		try{
			for(int i = 0; i < inputLevelIndex.size(); i++){
				if (getInputs().isEmpty()){
					inputLevelIndex.set(i, -1);
				}
				else if (getInputs().get(0).getDescriptor(this).getColumnCount() <= i){
					inputLevelIndex.set(i, -1);
				}
			}
			descriptor = new DefaultStreamDescriptor();
			int counter = 0;
			for(int i = 0; i < inputLevelIndex.size(); i++){
				
				try{
					if (inputLevelIndex.get(i) != -1){
						descriptor.addColumn(getInputs().get(0).getDescriptor(this).getStreamElements().get(inputLevelIndex.get(i)).clone(getName(), inputs.get(0).getName()));
					}
					else{
						descriptor.addColumn(getName(), "", "unknonw" + ++counter, 0, "java.lang.Object", "", true, "UNKNONW", "", false);
					}
				}catch(Exception ex){
					descriptor.addColumn(getName(), "", "unknonw" + ++counter, 0, "java.lang.Object", "", true, "UNKNONW", "", false);
				}
				
			}
		}catch(Exception ex){
			
		}
		
		
		
		for(Transformation t : outputs){
			t.refreshDescriptor();
		}
		
	}

	public Transformation copy() {
		
		return null;
	}

	public String getAutoDocumentationDetails() {
		StringBuffer buf = new StringBuffer();
		buf.append("Normalized Fields : \n");
		for(Integer i : inputLevelIndex){
			try{
				buf.append("\t- " + getDescriptor(this).getColumnName(i) + "\n");
			}catch(Exception ex){
				buf.append("\t- Field number " + i + "\n");
			}
		}
		return buf.toString();
	}
}
