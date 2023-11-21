package bpm.forms.design.ui.views.content.pages;

import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.Section;

import bpm.forms.core.design.IForm;
import bpm.forms.design.ui.Activator;
import bpm.forms.design.ui.Messages;
import bpm.forms.design.ui.composite.CompositeForm;
import bpm.forms.design.ui.composite.CompositeFormRuntime;
import bpm.forms.design.ui.composite.CompositeInstanciationRule;
import bpm.forms.design.ui.composite.tools.ExpantionListener;
import bpm.forms.design.ui.views.content.ContentMasterDetailsBlock;
import bpm.vanilla.platform.core.beans.Group;

public class PageForm implements IDetailsPage{
	private ExpantionListener expansionListener = new ExpantionListener();
	
	private CompositeForm formComposite;
	private CompositeFormRuntime formRuntime;
	private CompositeInstanciationRule instanciationRule;
	
	private boolean isDirty;
	private IManagedForm form;
	private ContentMasterDetailsBlock masterBlock;
	
	private CheckboxTableViewer validatorViewer;
	
	
	private ICheckStateListener checkListener = new ICheckStateListener() {
		
		@Override
		public void checkStateChanged(CheckStateChangedEvent event) {
			
			Group g = (Group)event.getElement();
			if (event.getChecked()){
				formComposite.getInput().addValidationGroup(g.getId());
			}
			else{
				formComposite.getInput().removeValidationGroup(g.getId());
			}
			
			isDirty = true;
			form.dirtyStateChanged();
			masterBlock.addDirtyObject(formComposite.getInput());

		}
	};
	
	public PageForm(ContentMasterDetailsBlock masterBlock){
		this.masterBlock = masterBlock;
	}
	
	
	
	@Override
	public void createContents(Composite parent) {
		parent.setLayout(new GridLayout());
		parent.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		
		Section section = form.getToolkit().createSection(parent, Section.TITLE_BAR | Section.TWISTIE);
		section.setLayout(new GridLayout());
		section.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
		section.addExpansionListener(expansionListener);
		
		expansionListener.registerSection(section);
		section.setText(Messages.PageForm_0);
		
		formComposite.createContent(section);
		formComposite.getClient().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		
		section.setClient(formComposite.getClient());
		
		formComposite.getClient().addListener(SWT.Modify, new Listener(){
			@Override
			public void handleEvent(Event event) {
				isDirty = true;
				form.dirtyStateChanged();
				masterBlock.addDirtyObject(formComposite.getInput());
				
			}
		});
		
		
		section.setClient(formComposite.getClient());
		
		
		/*
		 * validator
		 */
		createValidators(parent);
		
		/*
		 * instanciationRules
		 */
		createInstanciationRules(parent);
		
		
		section = form.getToolkit().createSection(parent, Section.TITLE_BAR | Section.TWISTIE);
		section.setLayout(new GridLayout());
		section.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
		
		section.setText(Messages.PageForm_1);
		section.addExpansionListener(expansionListener);
		
		expansionListener.registerSection(section);
		
		formRuntime.createContent(section);
		formRuntime.getClient().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));

		section.setClient(formRuntime.getClient());

		
	}
	
	private void createInstanciationRules(Composite parent){
		Section section = form.getToolkit().createSection(parent, Section.TITLE_BAR | Section.TWISTIE | Section.EXPANDED);
		section.setText(Messages.PageForm_2);
		section.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
		section.setExpanded(false);
		
		instanciationRule = new CompositeInstanciationRule(form.getToolkit());
		instanciationRule.createContent(section);
		instanciationRule.getClient().setLayoutData(new GridData(GridData.FILL_BOTH));
		
		section.setClient(instanciationRule.getClient());
		
		instanciationRule.getClient().addListener(SWT.Modify, new Listener(){
			@Override
			public void handleEvent(Event event) {
				isDirty = true;
				form.dirtyStateChanged();
				masterBlock.addDirtyObject(formComposite.getInput());
				
			}
		});
		section.addExpansionListener(expansionListener);
		
		expansionListener.registerSection(section);

	}

	
	
	
	
	private void createValidators(Composite parent){
		Section section = form.getToolkit().createSection(parent, Section.TITLE_BAR | Section.TWISTIE | Section.EXPANDED);
		section.setText(Messages.PageForm_3);
		section.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
		section.setExpanded(false);
		
		validatorViewer = new CheckboxTableViewer(form.getToolkit().createTable(section, SWT.CHECK | SWT.V_SCROLL | SWT.H_SCROLL));
		validatorViewer.getTable().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		validatorViewer.setContentProvider(new ArrayContentProvider());
		validatorViewer.setLabelProvider(new LabelProvider(){
			@Override
			public String getText(Object element) {
				return ((Group)element).getName();
			}
		});
		validatorViewer.getTable().setLinesVisible(true);
		validatorViewer.getTable().setHeaderVisible(true);
//		validatorViewer.getTable().getColumn(0).setText("Validator Group Name");
		
		
		section.setClient(validatorViewer.getControl());
		section.addExpansionListener(expansionListener);
		expansionListener.registerSection(section);
	}
	
	@Override
	public void commit(boolean onSave) {
		if (!onSave){
			return;
		}
		try{
			Activator.getDefault().getServiceProvider().getDefinitionService().update(formComposite.getInput());
			masterBlock.removeDirtyObject(formComposite.getInput());
			isDirty = false;
		}catch(Exception ex){
			ex.printStackTrace();
			MessageDialog.openError(formComposite.getClient().getShell(), Messages.PageForm_4, ex.getMessage());
		}
	}

	@Override
	public void dispose() {
		formComposite.getClient().dispose();
		
	}

	@Override
	public void initialize(IManagedForm form) {
		this.form = form;
		formComposite = new CompositeForm(form.getToolkit());
		formRuntime = new CompositeFormRuntime(form.getToolkit());
	}

	@Override
	public boolean isDirty() {
		return isDirty;
	}

	@Override
	public boolean isStale() {
		
		return false;
	}

	@Override
	public void refresh() {
		
		
	}

	@Override
	public void setFocus() {
		
		
	}

	@Override
	public boolean setFormInput(Object input) {
		
		return false;
	}

	@Override
	public void selectionChanged(IFormPart part, ISelection selection) {
		if (selection.isEmpty()){
			formComposite.getClient().setVisible(false);
			formRuntime.getClient().setVisible(false);
		}
		
		Object o = ((IStructuredSelection)selection).getFirstElement();
		if (o instanceof IForm){
			formComposite.setInput((IForm)o);
			
			validatorViewer.removeCheckStateListener(checkListener);
			/*
			 * fill Validators
			 */
			
			try{
				List<Group> l = Activator.getDefault().getVanillaContext().getVanillaApi().getVanillaSecurityManager().getGroups();
				validatorViewer.setInput(l);
				
				
				for(Group g : l){
					
					for(Integer i : ((IForm)o).getValidatorGroups()){
						if (g.getId() == i.intValue()){
							validatorViewer.setChecked(g, true);
							break;
						}
					}
				}
				
				
			}catch(Exception ex){
				ex.printStackTrace();
				MessageDialog.openError(formComposite.getClient().getShell(), Messages.PageForm_5, Messages.PageForm_6 + ex.getMessage());
			}
			validatorViewer.addCheckStateListener(checkListener);
			
			//TODO : reload from db and compare with O to know the if the state isDirty
			formComposite.getClient().setVisible(true);
			
			formRuntime.getClient().setVisible(true);
			formRuntime.setInput((IForm)o);
			
			/*
			 * fill InstanciationRule
			 */
//			instanciationRule.getClient().removeListener(SWT.Modify, fieldMappingListener);
			instanciationRule.setInput((IForm)o);
//			instanciationRule.getClient().addListener(SWT.Modify, fieldMappingListener);

		}
		else{
			formComposite.getClient().setVisible(false);
			formRuntime.getClient().setVisible(false);
		}
		
	}

	

}
