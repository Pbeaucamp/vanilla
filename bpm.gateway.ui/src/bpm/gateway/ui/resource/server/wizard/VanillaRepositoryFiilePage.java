package bpm.gateway.ui.resource.server.wizard;

import java.util.Properties;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import bpm.gateway.ui.i18n.Messages;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;
import bpm.vanilla.repository.ui.viewers.RepositoryContentProvider;
import bpm.vanilla.repository.ui.viewers.RepositoryLabelProvider;

public class VanillaRepositoryFiilePage extends RepositoryPage{

	private Button loadRepository;
	private ComboViewer repositories;
	
	protected VanillaRepositoryFiilePage(String pageName) {
		super(pageName);
		
	}

	protected void createCustomWidgets(Composite parent){
		loadRepository = new Button(parent, SWT.PUSH);
		loadRepository.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		loadRepository.setText(Messages.VanillaRepositoryFiilePage_0);
		loadRepository.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				IVanillaAPI api = new RemoteVanillaPlatform(url.getText(), login.getText(), password.getText());
				try{
					repositories.setInput(api.getVanillaRepositoryManager().getUserRepositories(login.getText()));
				}catch(Exception ex){
					ex.printStackTrace();
					MessageDialog.openError(getShell(), Messages.VanillaRepositoryFiilePage_7, Messages.VanillaRepositoryFiilePage_8 + ex.getMessage());
					return;
				}
			}
		});
	
		Label l = new Label(parent, SWT.NONE);
		l.setLayoutData(new GridData());
		l.setText(Messages.VanillaRepositoryFiilePage_9);
		
		repositories = new ComboViewer(parent, SWT.READ_ONLY);
		repositories.getControl().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		repositories.setContentProvider(new RepositoryContentProvider());
		repositories.setLabelProvider(new RepositoryLabelProvider());
		repositories.addSelectionChangedListener(new ISelectionChangedListener() {
			
			public void selectionChanged(SelectionChangedEvent event) {
				if (isPageComplete()){
					test.setEnabled(true);
				}
				else{
					test.setEnabled(false);
				}
				getContainer().updateButtons();
				
			}
		});
	}
	
	public Properties getValues() {
		Properties p = super.getValues();
		IStructuredSelection ss = (IStructuredSelection)repositories.getSelection();
		
		p.setProperty("repdefId",((Repository)ss.getFirstElement()).getId() + ""); //$NON-NLS-1$ //$NON-NLS-2$
		return p;
	}
	
	@Override
	public boolean isPageComplete() {
		return super.isPageComplete() && !repositories.getSelection().isEmpty();
	}
}
