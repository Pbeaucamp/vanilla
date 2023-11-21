package bpm.birep.admin.client.trees;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import adminbirep.Activator;




public class TreeLabelProvider extends LabelProvider {
	public String getText(Object obj) {
		
		return obj.toString();
	}
	public Image getImage(Object obj) {
		ImageRegistry reg = Activator.getDefault().getImageRegistry();
		if (obj instanceof TreeUser) {
			return reg.get("user"); //$NON-NLS-1$
		}
		else if (obj instanceof TreeGroup) {
			return reg.get("group"); //$NON-NLS-1$
		}
		else if (obj instanceof TreeRole) {
			return reg.get("role"); //$NON-NLS-1$
		}
		else if (obj instanceof TreeDirectory){
			return reg.get("folder"); //$NON-NLS-1$
			
		}
		else if (obj instanceof TreeDatasource){
			return reg.get("datasource"); //$NON-NLS-1$
		}
		else if (obj instanceof TreeVariable){
			return reg.get("variable"); //$NON-NLS-1$
		}
		else if (obj instanceof TreeItem){
			String key =  bpm.birep.admin.client.trees.TreeHelperType.getKeyForType(((TreeItem)obj).getItem().getType(), ((TreeItem)obj).getItem());
			return reg.get(key);
		}
				
		return reg.get("default"); //$NON-NLS-1$
	}
}
