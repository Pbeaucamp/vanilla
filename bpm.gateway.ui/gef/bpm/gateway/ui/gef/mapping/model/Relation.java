package bpm.gateway.ui.gef.mapping.model;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Composite;

import bpm.gateway.core.Transformation;
import bpm.gateway.core.transformations.SimpleMappingTransformation;
import bpm.gateway.core.transformations.outputs.IOutput;
import bpm.gateway.ui.i18n.Messages;


public class Relation {
	private FieldModel source;
	private FieldModel target;
	private boolean isConnected = false;
	private Composite parent;
	private Transformation input;
	
	public Relation(FieldModel source, FieldModel target, Composite parent)throws Exception{
		this.source = source;
		this.target = target;
		this.parent = parent;
		reconnect(source, target);
	}
	
	public FieldModel getSource(){
		return source;
	}
	
	public FieldModel getTarget(){
		return target;
	}
	
	public void reconnect()  throws Exception{
		if (!isConnected) {
				source.addRelation(this);			
				target.addRelation(this);
				
				if(source.getTransfo() instanceof IOutput) {
					((IOutput)source.getTransfo()).createMapping(input, source.getParent().getFields().indexOf(source),target.getParent().getFields().indexOf(target));
				}
				else {
					((SimpleMappingTransformation)source.getTransfo()).createMapping(source.getParent().getFields().indexOf(source),target.getParent().getFields().indexOf(target));
				}
			
			isConnected = true;
		}
	}
	
	public void reconnect(FieldModel newSource, FieldModel newTarget) throws Exception{
		if (newSource == null || newTarget == null || newSource == newTarget) {
			throw new IllegalArgumentException();
//			return;
		}
		disconnect();
		this.source = newSource;
		this.target = newTarget;
		
		for(Transformation t : source.getTransfo().getInputs() ){
			if(source.getTransfo() instanceof IOutput) {
				if(((IOutput)source.getTransfo()).getMappingsFor(t).keySet().contains(source.getFieldName()) && this.parent != null){
					MessageDialog.openError(this.parent.getShell(), Messages.DBOutputMappingSection_8, Messages.DBOutputMappingSection_10);
					isConnected = true;
				}else if (((IOutput)source.getTransfo()).getMappingsFor(t).values().contains(target.getFieldName()) && this.parent != null){
					MessageDialog.openError(this.parent.getShell(), Messages.DBOutputMappingSection_8, Messages.DBOutputMappingSection_11);
					isConnected = true;
				}
			}
			else {
				if(((SimpleMappingTransformation)source.getTransfo()).getMappings().keySet().contains(source.getFieldName()) && this.parent != null){
					MessageDialog.openError(this.parent.getShell(), Messages.DBOutputMappingSection_8, Messages.DBOutputMappingSection_10);
					isConnected = true;
				}else if (((SimpleMappingTransformation)source.getTransfo()).getMappings().values().contains(target.getFieldName()) && this.parent != null){
					MessageDialog.openError(this.parent.getShell(), Messages.DBOutputMappingSection_8, Messages.DBOutputMappingSection_11);
					isConnected = true;
				}
			}
			
			if(source.getParent().getName().equals(t.getName())){
				input = t;
				
			}
		}
		
		reconnect();
	}
	
	public void disconnect() {
		if (isConnected) {
			source.removeRelation(this);

			
			target.removeRelation(this);
			isConnected = false;
		}
	}
}
