package bpm.norparena.ui.menu.client.trees;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import bpm.norparena.ui.menu.Activator;
import bpm.norparena.ui.menu.icons.Icons;




public class TreeLabelProvider extends LabelProvider {
	public String getText(Object obj) {
		
		return obj.toString();
	}
	public Image getImage(Object obj) {
		ImageRegistry reg = Activator.getDefault().getImageRegistry();
		if (obj instanceof TreeUser) {
			return reg.get(Icons.USER);
		}
		else if (obj instanceof TreeGroup) {
			return reg.get(Icons.GROUP);
		}
//		else if (obj instanceof TreeRole) {
//			return reg.get("role");
//		}
		else if (obj instanceof TreeDirectory){
			return reg.get(Icons.FOLDER);
		}
//		else if (obj instanceof TreeDatasource){
//			return reg.get("datasource");
//		}
//		else if (obj instanceof TreeVariable){
//			return reg.get("variable");
//		}
		else if (obj instanceof TreeItem){
			String key =  bpm.norparena.ui.menu.client.trees.TreeHelperType.getKeyForType(((TreeItem)obj).getItem().getType(), ((TreeItem)obj).getItem());
			return reg.get(key);
		}
				
		return reg.get("default"); //$NON-NLS-1$
	}
}
