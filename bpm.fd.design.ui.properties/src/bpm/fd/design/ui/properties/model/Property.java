package bpm.fd.design.ui.properties.model;

import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.SimpleContentProposalProvider;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.jface.viewers.CellEditor;

import bpm.fd.design.ui.properties.i18n.Messages;

public class Property {
	private String name;
	private String value = ""; //$NON-NLS-1$
	private Property parent;
	private CellEditor cellEditor;	
	private String keyProperty;
	private ContentProposalAdapter contentAssist;
	
	public Property(String name, CellEditor cellEditor) {
		this.name = name;
		this.cellEditor = cellEditor;
	}
	public Property(String name, CellEditor cellEditor, boolean useContentAssist) {
		this.name = name;
		this.cellEditor = cellEditor;
		if (useContentAssist){
			contentAssist = new ContentProposalAdapter(
					cellEditor.getControl(), 
					new TextContentAdapter(), 
					new SimpleContentProposalProvider(new String[]{}), 
					null, 
					null);
		}
	}
	
	public void updateContentAssist(String[] values){
		if (contentAssist != null){
			contentAssist.setContentProposalProvider(new SimpleContentProposalProvider(values));
		}
		
	}
	public Property(String name, CellEditor cellEditor, String keyProperty) {
		this.name = name;
		this.cellEditor = cellEditor;
		this.keyProperty = keyProperty;
	}
	
	public String getKeyProperty(){
		return keyProperty;
	}

	/**
	 * @return the cellEditor
	 */
	public CellEditor getCellEditor() {
		return cellEditor;
	}

	/**
	 * @return the parent
	 */
	public Property getParent() {
		return parent;
	}

	/**
	 * @param parent the parent to set
	 */
	public void setParent(Property parent) {
		this.parent = parent;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
}
