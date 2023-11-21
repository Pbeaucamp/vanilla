package org.fasd.viewers;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.eclipse.swt.graphics.ImageData;
import org.fasd.olap.OLAPMeasure;
import org.fasd.utils.trees.TreeMes;
import org.freeolap.FreemetricsPlugin;

public class MeasureViewDecorator implements ILightweightLabelDecorator {

	private static ImageDescriptor paletteDecoration;

	static {
		ImageDescriptor img = FreemetricsPlugin.getDefault().getImageRegistry().getDescriptor("coloring"); //$NON-NLS-1$
		ImageData data = img.getImageData().scaledTo(10, 10);

		paletteDecoration = ImageDescriptor.createFromImageData(data);

	}

	public void decorate(Object element, IDecoration decoration) {
		if (!(element instanceof TreeMes)) {
			return;
		}

		OLAPMeasure m = ((TreeMes) element).getOLAPMeasure();

		if (m.getOrigin() != null) {
			return;
		}

		if (m.getColorScript() != null && !"".equals(m.getColorScript())) { //$NON-NLS-1$

			decoration.addOverlay(paletteDecoration, IDecoration.BOTTOM_RIGHT);
		}

	}

	public void addListener(ILabelProviderListener listener) {
	}

	public void dispose() {
	}

	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	public void removeListener(ILabelProviderListener listener) {
	}

}
