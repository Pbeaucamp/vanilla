package org.fasd.cubewizard.dimension.preload;

import org.eclipse.jface.viewers.LabelProvider;
import org.fasd.olap.OLAPDimension;
import org.fasd.olap.OLAPHierarchy;
import org.fasd.olap.OLAPLevel;
import org.fasd.olap.OLAPMeasure;

public class FasdModelElementsLabelProvider extends LabelProvider {

	@Override
	public String getText(Object element) {
		if (element instanceof OLAPDimension) {
			return ((OLAPDimension) element).getName();
		} else if (element instanceof OLAPHierarchy) {
			return ((OLAPHierarchy) element).getName();
		} else if (element instanceof OLAPLevel) {
			return ((OLAPLevel) element).getName();
		} else if (element instanceof OLAPMeasure) {
			return ((OLAPMeasure) element).getName();
		}

		return element.toString();
	}
}