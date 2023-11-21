package bpm.es.pack.manager.provider;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import adminbirep.icons.Icons;
import bpm.es.pack.manager.utils.ItemImage;
import bpm.vanilla.platform.core.repository.RepositoryDirectory;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class RepositoryLabelProvider extends LabelProvider{

	@Override
	public Image getImage(Object obj) {
		ImageRegistry reg = adminbirep.Activator.getDefault().getImageRegistry();

		if (obj instanceof RepositoryItem) {
			String key = ItemImage.getKeyForType(((RepositoryItem)obj).getType(), (RepositoryItem)obj);
			return reg.get(key);
		}
		else if (obj instanceof RepositoryDirectory){
			return reg.get(Icons.FOLDER);
		}
				
		return reg.get(Icons.DEFAULT);
	}

	@Override
	public String getText(Object element) {
		if (element instanceof RepositoryDirectory){
			return ((RepositoryDirectory)element).getName();
		}
		else if (element instanceof RepositoryItem){
			return ((RepositoryItem)element).getItemName();
		}
		return super.getText(element);
	}
}
