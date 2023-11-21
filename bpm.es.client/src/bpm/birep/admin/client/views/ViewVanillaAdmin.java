package bpm.birep.admin.client.views;

import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import adminbirep.Activator;
import adminbirep.Messages;
import bpm.birep.admin.client.composites.CompositeVanillaSetup;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.VanillaSetup;

public class ViewVanillaAdmin extends ViewPart {

	public static final String ID = "bpm.birep.admin.client.viewvanillaadmin"; //$NON-NLS-1$
	private Composite properties;
	
	@Override
	public void createPartControl(Composite parent) {

		try {
			String login = Activator.getDefault().getRepositoryApi().getContext().getVanillaContext().getLogin();
			
			List<User> users = Activator.getDefault().getVanillaApi().getVanillaSecurityManager().getUsers();
			User user = null;
			
			
			for(User u : users){
				if (u.getLogin().equals(login)) {
					user = u;
					break;
				}
			}
			
			
	
			if (user.isSuperUser() == true){
				properties = new CompositeVanillaSetup(parent, SWT.NONE, Activator.getDefault().getVanillaApi().getVanillaSystemManager().getVanillaSetup());
				properties.setLayoutData(new GridData(GridData.FILL_BOTH));
				parent.layout();
			}
			else{
				MessageDialog.openWarning(getSite().getShell(), Messages.Client_Views_ViewVanillaAdmin_0, Messages.Client_Views_ViewVanillaAdmin_2);
			}
		}
		catch (Exception e){
			try{
				VanillaSetup vanillaSetup = Activator.getDefault().getVanillaApi().getVanillaSystemManager().getVanillaSetup();
				properties = new CompositeVanillaSetup(parent, SWT.NONE, vanillaSetup);
				properties.setLayoutData(new GridData(GridData.FILL_BOTH));
				parent.layout();
			}catch(Exception ex){
				ex.printStackTrace();
				MessageDialog.openError(getSite().getShell(), Messages.Client_Views_ViewVanillaAdmin_3, Messages.Client_Views_ViewVanillaAdmin_4 + ex.getMessage());
			}
		
		}

	}

	@Override
	public void setFocus() { }

}
