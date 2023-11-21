package bpm.fd.design.ui.component.wizard;

import java.util.Properties;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IWorkbench;

import bpm.fd.api.core.model.components.definition.ComponentParameter;
import bpm.fd.api.core.model.components.definition.IComponentDefinition;
import bpm.fd.api.core.model.components.definition.comment.CommentOptions;
import bpm.fd.api.core.model.components.definition.comment.ComponentComment;
import bpm.fd.api.core.model.resources.Dictionary;
import bpm.fd.api.core.model.resources.exception.DictionaryException;
import bpm.fd.design.ui.Activator;
import bpm.fd.design.ui.component.Messages;
import bpm.fd.design.ui.component.comment.CommentPage;
import bpm.fd.design.ui.component.pages.GeneralPage;
import bpm.fd.design.ui.wizard.IWizardComponent;

public class WizardComment extends Wizard implements IWizardComponent{

	private GeneralPage generic;
	private CommentPage commentOpt;
	
	private IComponentDefinition def;
	
	@Override
	public boolean performFinish() {
		Dictionary dictionaty = Activator.getDefault().getProject().getDictionary();
		Properties prop = generic.getValues();
		
		ComponentComment component = new ComponentComment(prop.getProperty(GeneralPage.PROPERTY_NAME), dictionaty);
		component.setName(prop.getProperty(GeneralPage.PROPERTY_NAME));
		component.setComment(prop.getProperty(GeneralPage.PROPERTY_DESCRIPTION));

		CommentOptions opt = new CommentOptions();
		
		opt.setLimit(commentOpt.getLimit());
		opt.setLimitComment(commentOpt.asLimit());
		opt.setValidation(commentOpt.isValidation());
		
		for(ComponentParameter p : commentOpt.getParameters()) {
			component.addComponentParameter(p);
		}
		
		component.setComponentOption(opt);
		
		try {
			dictionaty.addComponent(component);
			
		} catch (DictionaryException e) {
			ErrorDialog.openError(getShell(), Messages.WizardComment_0, Messages.WizardComment_1, 
					new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e));
			return false;
		}
		
		Activator.getDefault().firePropertyChange(Activator.PROJECT_CONTENT, null, component);
		
		def = component;
		return true;
	}

	@Override
	public IComponentDefinition getComponent() {
		return def;
	}

	@Override
	public Class<? extends IComponentDefinition> getComponentClass() {
		return ComponentComment.class;
	}

	@Override
	public boolean needRepositoryConnections() {
		return false;
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		
		
	}

	@Override
	public void addPages() {
		generic = new GeneralPage();
		addPage(generic);
		
		commentOpt = new CommentPage();
		addPage(commentOpt);
		
		super.addPages();
	}

}
