package org.fasd.views.actions;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.fasd.i18N.LanguageText;
import org.fasd.views.dialogs.DialogVirtualCube;
import org.freeolap.FreemetricsPlugin;

public class ActionAddVirtual extends Action {
	
	public ActionAddVirtual(){
		super(LanguageText.ActionAddVirtual_New);
		Image img = new Image(FreemetricsPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell().getDisplay(),Platform.getInstallLocation().getURL().getPath() +"icons/vcube.png"); //$NON-NLS-1$
		this.setImageDescriptor(ImageDescriptor.createFromImage(img));	

	}
	public void run(){
		DialogVirtualCube dial = new DialogVirtualCube(FreemetricsPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell());
		if (dial.open() == DialogVirtualCube.OK){
			FreemetricsPlugin.getDefault().getFAModel().getOLAPSchema().addVirtualCube(dial.getVirtualCube());
			FreemetricsPlugin.getDefault().getFAModel().getOLAPSchema().getListVirtualCube().setChanged();
			FreemetricsPlugin.getDefault().getFAModel().setChanged();
		}
	}
}
