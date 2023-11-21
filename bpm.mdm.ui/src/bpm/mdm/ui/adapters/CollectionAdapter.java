package bpm.mdm.ui.adapters;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.util.EContentAdapter;

import bpm.mdm.ui.Activator;

public class CollectionAdapter extends EContentAdapter{
	@Override
	public void notifyChanged(Notification notification) {
		Activator.getDefault().getControler().setModelDirty();
		
	}
}
