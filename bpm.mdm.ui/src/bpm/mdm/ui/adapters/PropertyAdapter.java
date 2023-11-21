package bpm.mdm.ui.adapters;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;

import bpm.mdm.model.MdmFactory;
import bpm.mdm.ui.Activator;

public class PropertyAdapter extends AdapterImpl{
	@Override
	public void notifyChanged(Notification msg) {
		/*
		 * this property is not stored within the model
		 * but only used for display 
		 */
		if (!(MdmFactory.eINSTANCE.getMdmPackage().getSynchronizer_DataSourceName() == msg.getFeature())){
			Activator.getDefault().getControler().setModelDirty();
		}
		

	}

}
