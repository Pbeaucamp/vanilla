package bpm.mdm.ui.wizards.pages;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import bpm.mdm.model.Attribute;
import bpm.mdm.model.Entity;
import bpm.mdm.model.Rule;
import bpm.mdm.model.rules.EntityLinkRule;
import bpm.mdm.ui.Activator;
import bpm.mdm.ui.i18n.Messages;
import bpm.mdm.ui.model.composites.viewer.MdmLabelProvider;

public class LinkagePage extends WizardPage implements IRulePage{

	private ComboViewer entities;
	private ComboViewer attributes;
	
	
	private Rule editedRule;
	public LinkagePage(String pageName) {
		super(pageName);
		
	}

	public LinkagePage(String pageName, Rule editedrule) {
		this(pageName);
		this.editedRule= editedrule;
	}

	@Override
	public void createControl(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout(2, false));
		
		Label l = new Label(main, SWT.NONE);
		l.setLayoutData(new GridData());
		l.setText(Messages.LinkagePage_0);
		
		entities = new ComboViewer(main, SWT.READ_ONLY);
		entities.getControl().setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
		entities.setContentProvider(new ArrayContentProvider());
		entities.setLabelProvider(new MdmLabelProvider());
		entities.addSelectionChangedListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				Entity e = (Entity)((IStructuredSelection)event.getSelection()).getFirstElement();
				attributes.setInput(e.getAttributes());
				
			}
		});
		entities.setInput(Activator.getDefault().getMdmProvider().getModel().getEntities());
		
		l = new Label(main, SWT.NONE);
		l.setLayoutData(new GridData());
		l.setText(Messages.LinkagePage_1);
		
		attributes = new ComboViewer(main, SWT.READ_ONLY);
		attributes.getControl().setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
		attributes.setContentProvider(new ArrayContentProvider());
		attributes.setLabelProvider(new MdmLabelProvider());
		
		
		setControl(main);
		fill();
	}
	private void fill(){
		if (editedRule != null){
			if (((EntityLinkRule)editedRule).getLinkedAttribute() != null){
				entities.setSelection(new StructuredSelection(((EntityLinkRule)editedRule).getLinkedAttribute().eContainer()));
				attributes.setSelection(new StructuredSelection(((EntityLinkRule)editedRule).getLinkedAttribute()));
			}
		}
		
		attributes.addSelectionChangedListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				if (getContainer() != null){
					getContainer().updateButtons();
				}
				
				
			}
		});
	}
	@Override
	public boolean isPageComplete() {
		
		return !attributes.getSelection().isEmpty();
	}
	
	@Override
	public boolean canFlipToNextPage() {
		return false;
	}

	@Override
	public void setRule(Rule rule) {
		Attribute a = (Attribute)((IStructuredSelection)attributes.getSelection()).getFirstElement();
		((EntityLinkRule)rule).setLinkedAttribute(a);
		
	}
	
}
