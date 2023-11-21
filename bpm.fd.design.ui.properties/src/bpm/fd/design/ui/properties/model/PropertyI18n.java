package bpm.fd.design.ui.properties.model;

import java.io.FileOutputStream;
import java.util.Properties;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.CellEditor;

import bpm.fd.design.ui.properties.i18n.Messages;

public class PropertyI18n extends Property{
	private Properties props;
	private String key;
	private IFile resourceFile;
	public PropertyI18n(String name, String key, IFile resourceFile, Properties props, CellEditor cellEditor) {
		super(name, cellEditor);
		this.props = props;
		this.key = key;
		this.resourceFile = resourceFile;
	}
	
	public String getPropertyValue(){
		String s = props.getProperty(key);
		if (s == null){
			return ""; //$NON-NLS-1$
		}
		return s;
			
	}
	

	public void setPropertyValue(Object value){
		props.setProperty(key, (String)value);
		try {
			props.store(new FileOutputStream(resourceFile.getLocation().toOSString()), ""); //$NON-NLS-1$
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		
	}

}
