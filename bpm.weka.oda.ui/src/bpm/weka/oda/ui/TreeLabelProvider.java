package bpm.weka.oda.ui;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import bpm.vanilla.platform.core.repository.RepositoryDirectory;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class TreeLabelProvider extends LabelProvider {
	
	private Image directory = ImageDescriptor.createFromFile(TreeLabelProvider.class, "directory.png").createImage();
	private Image item = ImageDescriptor.createFromFile(TreeLabelProvider.class, "item.png").createImage();
	
	@Override
	public String getText(Object obj) {
		if (obj instanceof RepositoryDirectory){
			return ((RepositoryDirectory)obj).getName();
		}
		else if (obj instanceof RepositoryItem){
			return ((RepositoryItem)obj).getName();
		}
		return obj.toString();
	}
	@Override
	public Image getImage(Object obj) {
		if (obj instanceof RepositoryDirectory){
			return directory; //$NON-NLS-1$
		}
		else if (obj instanceof RepositoryItem){
			return item; //$NON-NLS-1$
		}
		return null;
	}
}
