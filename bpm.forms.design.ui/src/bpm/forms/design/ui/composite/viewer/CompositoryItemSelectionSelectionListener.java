package bpm.forms.design.ui.composite.viewer;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;

import bpm.forms.core.design.IFormDefinition;
import bpm.forms.core.design.IFormUi;
import bpm.forms.core.design.ui.VanillaFdProperties;
import bpm.forms.design.ui.Activator;

import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.repository.ui.composites.CompositeRepositoryItemSelecter;

public class CompositoryItemSelectionSelectionListener implements ISelectionChangedListener{

	
	private IFormDefinition formDef;
	private CompositeRepositoryItemSelecter listenedComposite;
	private Composite compositeToNotify;
	
	public CompositoryItemSelectionSelectionListener(CompositeRepositoryItemSelecter listenedComposite, Composite compositeToNotify){
		this.listenedComposite = listenedComposite;
		this.compositeToNotify = compositeToNotify;
	}
	
	
	
	/**
	 * @return the formDef
	 */
	public IFormDefinition getFormDef() {
		return formDef;
	}



	/**
	 * @param formDef the formDef to set
	 */
	public void setFormDef(IFormDefinition formDef) {
		this.formDef = formDef;
	}



	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		IFormUi ui = formDef.getFormUI();
		if (ui == null){
			ui = Activator.getDefault().getFactoryModel().createFormUi();
			
			ui.setFormDefinitionId(formDef.getId());
			((IFormDefinition)formDef).setFormUI(ui);
		}
		
		if (listenedComposite.getSelectedItems().isEmpty() || !(listenedComposite.getSelectedItems().get(0) instanceof RepositoryItem)){
			ui.setProperty(VanillaFdProperties.PROP_FD_DIRECTORY_ITEM_ID, "0"); //$NON-NLS-1$
			ui.setName(""); //$NON-NLS-1$
			compositeToNotify.notifyListeners(SWT.Modify, new Event());
			return;
		}
		else{
			ui.setName(((RepositoryItem)listenedComposite.getSelectedItems().get(0)).getItemName());
			ui.setProperty(VanillaFdProperties.PROP_FD_DIRECTORY_ITEM_ID, ((RepositoryItem)listenedComposite.getSelectedItems().get(0)).getId() + ""); //$NON-NLS-1$
			compositeToNotify.notifyListeners(SWT.Modify, new Event());
		}
		
	}
}
