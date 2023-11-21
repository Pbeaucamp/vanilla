package groupviewer.tree;

import org.eclipse.jface.viewers.LabelProvider;

public class StructureLabelProvider extends LabelProvider {
	@Override
	public String getText(Object element) {
		if (element instanceof TreeObject)
			return ((TreeObject)element).getName();
		return super.getText(element);
	}
}
