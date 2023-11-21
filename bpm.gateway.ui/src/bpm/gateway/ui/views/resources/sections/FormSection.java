package bpm.gateway.ui.views.resources.sections;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import bpm.gateway.core.forms.Form;
import bpm.gateway.ui.Activator;
import bpm.gateway.ui.composites.CompositeFormMapping;
import bpm.gateway.ui.i18n.Messages;
import bpm.vanilla.platform.core.repository.IRepository;
import bpm.vanilla.platform.core.repository.Repository;


public class FormSection extends AbstractPropertySection {

	private Form form;
	private IRepository rep ; 
	
	private CompositeFormMapping composite;
	
	public FormSection() {
		
	}
	
	@Override
	public void createControls(Composite parent,
			TabbedPropertySheetPage tabbedPropertySheetPage) {

		super.createControls(parent, tabbedPropertySheetPage);
		
		Composite container = getWidgetFactory().createFlatFormComposite(parent);
		container.setLayout(new GridLayout());
		
		
		composite = new CompositeFormMapping(container, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));


	}
	
	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
        Assert.isTrue(selection instanceof IStructuredSelection);
        Object input = ((IStructuredSelection) selection).getFirstElement();
        Assert.isTrue(input instanceof Form);
        this.form = ((Form) input);
	}

	@Override
	public void refresh() {
		composite.setInput(form);
		
		
		BusyIndicator.showWhile( Display.getDefault(), new Runnable(){
			public void run(){
				try{
					if (rep == null || rep.getItem(form.getDirectoryItemId()) == null){
						rep = new Repository(Activator.getDefault().getRepositoryConnection());
					}
				}catch(Exception e){
					e.printStackTrace();
					MessageDialog.openWarning(getPart().getSite().getShell(), Messages.FormSection_0, e.getMessage());
				}	
			}
		});
	
		
		try{
			composite.fill( Activator.getDefault().getRepositoryConnection(), rep.getItem(form.getDirectoryItemId()));
		}catch(Exception e){
			e.printStackTrace();
			MessageDialog.openWarning(getPart().getSite().getShell(), Messages.FormSection_1, e.getMessage());
		}
		
		super.refresh();
	}

	@Override
	public void aboutToBeHidden() {
		composite.removeListners();
		super.aboutToBeHidden();
	}

	@Override
	public void aboutToBeShown() {
		composite.addListners();
		super.aboutToBeShown();
	}

}
