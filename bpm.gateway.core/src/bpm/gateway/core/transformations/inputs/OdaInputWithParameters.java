package bpm.gateway.core.transformations.inputs;

import java.util.Enumeration;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.gateway.core.Transformation;
import bpm.gateway.core.transformations.inputs.odaconsumer.OdaHelper;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.transformations.inputs.RunOdaInput;
import bpm.gateway.runtime2.transformations.inputs.RunOdaInputWithParameters;
import bpm.vanilla.platform.core.IRepositoryContext;

public class OdaInputWithParameters extends OdaInput{

	@Override
	public RuntimeStep getExecutioner(IRepositoryContext repositoryCtx, int bufferSize) {
		return new RunOdaInputWithParameters(this, bufferSize);
	}

	@Override
	public void refreshDescriptor() {
		
		if (!isInited()){
			return;
		}
		try {
			//descriptor = OdaHelper.createDescriptor(this);
			descriptor = OdaHelper.createDescriptorWithParameters(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		for(Transformation t : getOutputs()){
			t.refreshDescriptor();
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Element getElement() {
		Element e = DocumentHelper.createElement("odaInputWithParameters");
		
		e.addElement("name").setText(name);
		e.addElement("description").setText(description);
		
		
		e.addElement("positionX").setText("" + x);
		e.addElement("positionY").setText("" + y);
		e.addElement("temporaryFileName").setText(getTemporaryFilename());
		e.addElement("temporarySeparator").setText(getTemporarySpliterChar() + "");
		
		if (getOdaExtensionId() != null){
			e.addElement("odaExtensionId").setText(getOdaExtensionId());
		}
		
		if (getOdaExtensionDataSourceId() != null){
			e.addElement("odaExtensionDataSourceId").setText(getOdaExtensionDataSourceId());
		}
		
		
		
		Element p = null;
		Enumeration<String> en =null;
		
		if (getDatasourcePublicProperties() != null){
			p = e.addElement("publicDataSource");
			en = (Enumeration<String>)getDatasourcePublicProperties().propertyNames();
			
			while(en.hasMoreElements()){
				String pname = en.nextElement();
				Element _p = p.addElement("property");
				_p.addElement("name").setText(pname);
				
				_p.addElement("value").setText(getDatasourcePublicProperties().getProperty(pname));
			}
		}
		
		if (getDatasourcePrivateProperties() != null){
			p = e.addElement("privateDataSource");
			en = (Enumeration<String>)getDatasourcePrivateProperties().propertyNames();
			
			while(en.hasMoreElements()){
				String pname = en.nextElement();
				Element _p = p.addElement("property");
				_p.addElement("name").setText(pname);
				_p.addElement("value").setText(getDatasourcePrivateProperties().getProperty(pname));
			}
		}
		
		
		if (getDatasetPrivateProperties() != null){
			p = e.addElement("privateDataSet");
			en = (Enumeration<String>)getDatasetPrivateProperties().propertyNames();
			
			while(en.hasMoreElements()){
				String pname = en.nextElement();
				Element _p = p.addElement("property");
				_p.addElement("name").setText(pname);
				_p.addElement("value").setText(getDatasetPrivateProperties().getProperty(pname));
			}
		}
		
		if (getDatasetPublicProperties() != null){
			p = e.addElement("publicDataSet");
			en = (Enumeration<String>)getDatasetPublicProperties().propertyNames();
			
			while(en.hasMoreElements()){
				String pname = en.nextElement();
				Element _p = p.addElement("property");
				_p.addElement("name").setText(pname);
				_p.addElement("value").setText(getDatasetPublicProperties().getProperty(pname));
			}
		}
		
		if (getQueryText() != null){
			e.addElement("queryText").setText(getQueryText());
		}
		
		
		for(String s : getParameterNames()){
			p = e.addElement("parameter");
			p.addElement("name").setText(s);
			Integer i =getParameterValue(s) ;
			
			if (i != null){
				p.addElement("value").setText( i + "");
			}
			else{
				p.addElement("value");
			}
			
			
		}
		return e;
	}
}
