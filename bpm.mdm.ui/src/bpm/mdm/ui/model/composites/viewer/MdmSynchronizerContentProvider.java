package bpm.mdm.ui.model.composites.viewer;

import bpm.mdm.model.Model;

public class MdmSynchronizerContentProvider extends MdmContentProvider{

	@Override
	public Object[] getChildren(Object parentElement) {
		
		if (parentElement instanceof Model){
			return((Model)parentElement).getSynchronizers().toArray();
		}

		return null;
	}
}
