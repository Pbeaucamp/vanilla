package bpm.metadata.birt.oda.ui.trees;


import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import bpm.metadata.birt.oda.ui.Activator;
import bpm.metadata.resource.ComplexFilter;
import bpm.metadata.resource.Filter;
import bpm.metadata.resource.SqlQueryFilter;
import bpm.vanilla.platform.core.IRepositoryApi;




public class TreeLabelProvider extends LabelProvider {
	
	private static final Image[] img = new Image[]{};
	
	@Override
	public String getText(Object obj) {
		return obj.toString();
	}
	@Override
	public Image getImage(Object obj) {
		if (obj instanceof TreeDirectory){
			return Activator.getDefault().getImageRegistry().get("directory"); //$NON-NLS-1$
		}
		if (obj instanceof TreeItem && ((TreeItem)obj).getItem().getType() == IRepositoryApi.CUST_TYPE){
			return Activator.getDefault().getImageRegistry().get("directoryItemBirt"); //$NON-NLS-1$
		}
		if (obj instanceof TreeItem){
			return Activator.getDefault().getImageRegistry().get("directoryItem"); //$NON-NLS-1$
		}
		if (obj instanceof TreeBusinessTable){
			return Activator.getDefault().getImageRegistry().get("table"); //$NON-NLS-1$
		}
		if (obj instanceof TreeDataStreamElement){
			return Activator.getDefault().getImageRegistry().get("column"); //$NON-NLS-1$
		}
		if (obj instanceof TreePrompt){
			return Activator.getDefault().getImageRegistry().get("prompt"); //$NON-NLS-1$
		}
		if (obj instanceof TreeFilter){
			if (((TreeFilter)obj).getFilter() instanceof ComplexFilter){
				return Activator.getDefault().getImageRegistry().get("complexFilter");
			}
			else if (((TreeFilter)obj).getFilter()  instanceof SqlQueryFilter){
				return Activator.getDefault().getImageRegistry().get("sqlFilter");
				
			}
			else if (((TreeFilter)obj).getFilter()  instanceof Filter){
				return Activator.getDefault().getImageRegistry().get("filter");
			}
			
			return Activator.getDefault().getImageRegistry().get("filter");

		}
		return null;
	}

}
