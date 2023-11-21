package bpm.gateway.core.transformations.calcul;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.gateway.core.AbstractTransformation;
import bpm.gateway.core.DefaultStreamDescriptor;
import bpm.gateway.core.GatewayDigester;
import bpm.gateway.core.StreamDescriptor;
import bpm.gateway.core.StreamElement;
import bpm.gateway.core.Transformation;
import bpm.gateway.core.exception.ServerException;
import bpm.gateway.core.server.userdefined.Variable;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.transformation.calculation.RuntimeCalculation;
import bpm.vanilla.platform.core.IRepositoryContext;

public class Calculation extends AbstractTransformation {
	public static final String PREBUILT_FUNCTIONS = 
		"function dateDifference(date1, date2){\n" +
		"     var d1 = new Date();\n" +
		"     d1.setTime(date1);\n" +
		"     var d2 = new Date();\n" +
		"     d2.setTime(date2);\n" +
		"     var d3 = new Date();\n" +
		"     d3.setTime(date1-date2);\n" +
		"     return d3;\n" +
		"}\n";
	
	
	private DefaultStreamDescriptor descriptor = new DefaultStreamDescriptor();
	
	
	private List<Script> scripts = new ArrayList<Script>();
	
	
	
	@Override
	public StreamDescriptor getDescriptor(Transformation transfo) throws ServerException {
		return descriptor;
	}

	@Override
	public Element getElement() {
		Element e = DocumentHelper.createElement("calculation");
		e.addElement("name").setText(name);
		e.addElement("description").setText(description);
		
		
		e.addElement("positionX").setText("" + x);
		e.addElement("positionY").setText("" + y);
		e.addElement("temporaryFileName").setText(getTemporaryFilename());
		e.addElement("temporarySeparator").setText(getTemporarySpliterChar() + "");
		
		for(Script s : scripts){
			Element c = e.addElement("script");
			c.addElement("name").setText(s.getName());
			c.addElement("expression").setText(s.getScriptFunction());
			c.addElement("type").setText(s.getType()+ "");
		}
		
		return e;
	}

//	@Override
//	public TransformationRuntime getExecutioner(RuntimeEngine runtimeEngine) {
//		return new CalculationRuntime(this, runtimeEngine);
//	}
	
	@Override
	public RuntimeStep getExecutioner(IRepositoryContext repositoryCtx, int bufferSize) {
		return new RuntimeCalculation(this, bufferSize);
	}

	@Override
	public void refreshDescriptor() {
		
		descriptor = new DefaultStreamDescriptor();
		if (!isInited()){
			return;
		}
		try{
			for(Transformation t : getInputs()){
				for (StreamElement e : t.getDescriptor(this).getStreamElements()){
					descriptor.addColumn(e.clone(getName(), t.getName()));
				}
			}
		}catch(Exception e){
			
		}

		
		Throwable th = new Throwable();
		boolean comeFromDigester = false;
		for(StackTraceElement st : th.getStackTrace()){
			if (st.getClassName().equals(GatewayDigester.class.getName())){
				comeFromDigester = true;
				break;
			}
		}
		
		if (inputs.size() > 0 ){
		
			
			List<Script> toRemove = new ArrayList<Script>();
			for(Script s : scripts){
				StreamElement e = new StreamElement();
				
				e.name = s.getName();
				e.transfoName = getName();
				e.originTransfo = getName();
				e.type = s.getType();
				e.typeName = Variable.VARIABLES_TYPES[e.type];
				
				switch(e.type){
				case Variable.BOOLEAN:
					e.className = Boolean.class.getName();
					break;
				case Variable.DATE:
					e.className = Date.class.getName();
					break;
				case Variable.FLOAT:
					e.className = Double.class.getName();
					break;
				case Variable.INTEGER:
					e.className = Integer.class.getName();
					break;
				case Variable.STRING:
					e.className = String.class.getName();
					break;
				default:
					e.className = Object.class.getName();
					
				}
				
				descriptor.addColumn(e);
			}
			
			if (!comeFromDigester){
				for(Script s : scripts){
					boolean contained = false;
					for(StreamElement e : descriptor.getStreamElements()){
						if (s.getScriptFunction().equals("") || s.getScriptFunction().contains("{$" + e.name + "}")){
							contained = true;
							break;
						}
					}
					
//					if (!contained){
//						toRemove.add(s);
//					}
					
				}
				
				if (!toRemove.isEmpty()){
					scripts.removeAll(toRemove);
					refreshDescriptor();
				}
			}
			
		}
		
		
		
		
		
		for(Transformation t : getOutputs()){
			t.refreshDescriptor();
		}

	}

	public boolean addScript(Script s){
		for(Script _s : scripts){
			if (s.getName().equals(_s.getName())){
				return false;
			}
		}
//		s.setOwner(this);
		scripts.add(s);
		
		refreshDescriptor();
		return true;
	}
	
	public boolean removeScript(Script s){
		boolean b = scripts.remove(s);
		if (b){
			refreshDescriptor();
		}
		return b;
	}
	
	public List<Script> getScripts(){
		return new ArrayList<Script>(scripts);
	}

	
	public Transformation copy() {
		Calculation copy = new Calculation();
		
		
		copy.setDescription(description);
		copy.setName("copy of " + name);
		

		return copy;
	}
	
	@Override
	public boolean addInput(Transformation stream) throws Exception {
		if (getInputs().size() > 0 && !getInputs().contains(stream)){
			throw new Exception("The Calculation Transformations can only have one Input");
		}
		boolean b =  super.addInput(stream);
		
		if (b){
			refreshDescriptor();
		}
		return b;
	}
	
	public String getAutoDocumentationDetails() {
		StringBuffer buf = new StringBuffer();
		for(Script sc : scripts){
			buf.append("Field Name : " + sc.getName() + "\n");
			buf.append("Field Type : " + sc.getType() + "\n");
			buf.append("Script : \n" + sc.getScriptFunction() + "\n");
		}
		
	
		return buf.toString();
	}
}
