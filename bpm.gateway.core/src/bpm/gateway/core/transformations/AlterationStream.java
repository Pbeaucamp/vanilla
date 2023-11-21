package bpm.gateway.core.transformations;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.gateway.core.AbstractTransformation;
import bpm.gateway.core.DefaultStreamDescriptor;
import bpm.gateway.core.StreamDescriptor;
import bpm.gateway.core.StreamElement;
import bpm.gateway.core.Transformation;
import bpm.gateway.core.exception.ServerException;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.vanilla.platform.core.IRepositoryContext;
/**
 * this class act as a description to perform some Transtype action nor 
 * format on Datas (Number, Date).
 * @author LCA
 *
 */
public class AlterationStream extends AbstractTransformation {
	public static final String[] DATE_FORMATS = new String[]{"yyyy", "yyyy-MM", "yyyy-MM-dd" , "yyyy-MM-dd HH:mm", "yyyy-MM-dd HH:mm,ss"};
	
	
	
	private List<AlterationInfo> alterations = new ArrayList<AlterationInfo>();
	private DefaultStreamDescriptor descriptor = new DefaultStreamDescriptor();
	
	
	
	
	@Override
	public boolean addInput(Transformation stream) throws Exception {
		
		if (getInputs().size() > 0 && !getInputs().contains(stream)){
			throw new Exception("The Filter Transformations can only have one Input");
		}
		
		boolean result = super.addInput(stream);
		if (result){
			refreshDescriptor();
		}	
		return result;
	}

	@Override
	public void removeInput(Transformation transfo) {
		List<AlterationInfo> toRemove = new ArrayList<AlterationInfo>();
			
		alterations.clear();
		super.removeInput(transfo);
	}

	public final List<AlterationInfo> getAlterations() {
		return alterations;
	}

	@Override
	public StreamDescriptor getDescriptor(Transformation transfo) throws ServerException {
				
		return descriptor;
	}

	


	@Override
	public Element getElement() {
		Element e = DocumentHelper.createElement("alterationStream");
		e.addElement("name").setText(name);
		e.addElement("description").setText(description);
		
		
		e.addElement("positionX").setText("" + x);
		e.addElement("positionY").setText("" + y);
		e.addElement("temporaryFileName").setText(getTemporaryFilename());
		e.addElement("temporarySeparator").setText(getTemporarySpliterChar() + "");

		
		for(AlterationInfo ai : alterations){
			e.add(ai.getElement());
		}
		
		return e;

	}

//	@Override
//	public TransformationRuntime getExecutioner(RuntimeEngine runtimeEngine) {
//		return new AlterationStreamRuntime(this, runtimeEngine);
//	}

	@Override
	public RuntimeStep getExecutioner(IRepositoryContext repositoryCtx, int bufferSize) {
		return null;
	}
	
	@Override
	public void refreshDescriptor() {
		descriptor = new DefaultStreamDescriptor();
		
		if (!isInited()){
			return;
		}
		
		boolean isFromDig = false;
		if (!alterations.isEmpty()){
			isFromDig = true;
		}

		
		for(Transformation t : getInputs()){
			
			try{
				for(StreamElement e : t.getDescriptor(this).getStreamElements()){
					
					StreamElement _e = e.clone(getName(), t.getName());
					descriptor.addColumn(_e);
					
					if (!isFromDig){
						AlterationInfo ai = new AlterationInfo();
						ai.setClassName(_e.className);
						ai.setFormat("");
						ai.setStreamElement(e);
						alterations.add(ai);
					}
					else{
						int i = t.getDescriptor(this).getStreamElements().indexOf(e);
						
						alterations.get(i).setStreamElement(e);
						_e.className = alterations.get(i).getClassName(); 
					}
					
				}
			}catch(Exception ex){
				
			}
			
			
		}
		
		
	}

	public Transformation copy() {
		
		return null;
	}

	public void addAlterationInfo(AlterationInfo info){
		alterations.add(info);
	}

	public String getAutoDocumentationDetails() {
		
		return "";
	}
}
