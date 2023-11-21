package bpm.es.pack.manager.provider;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import adminbirep.icons.Icons;
import bpm.es.pack.manager.utils.ItemImage;
import bpm.vanilla.workplace.core.model.PlaceImportDirectory;
import bpm.vanilla.workplace.core.model.PlaceImportItem;

public class PackageLabelProvider extends LabelProvider{

	@Override
	public Image getImage(Object obj) {
		ImageRegistry reg = adminbirep.Activator.getDefault().getImageRegistry();

		if (obj instanceof PlaceImportItem) {
			String key = ItemImage.getKeyForType(((PlaceImportItem)obj).getItem().getType(), ((PlaceImportItem) obj).getItem());
			return reg.get(key);
		}
		else if (obj instanceof PlaceImportDirectory){
			return reg.get(Icons.FOLDER);
		}
				
		return reg.get(Icons.DEFAULT);
	}

	@Override
	public String getText(Object element) {
		if (element instanceof PlaceImportDirectory){
			return ((PlaceImportDirectory)element).getName();
		}
		else if (element instanceof PlaceImportItem){
			return ((PlaceImportItem)element).getItem().getItemName();
		}
		return super.getText(element);
	}
}
