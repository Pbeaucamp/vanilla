package bpm.birt.osm.ui.reportitem;

import org.eclipse.birt.report.designer.ui.extensions.IReportItemImageProvider;
import org.eclipse.birt.report.model.api.ExtendedItemHandle;
import org.eclipse.birt.report.model.api.metadata.DimensionValue;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import bpm.birt.osm.ui.Activator;
import bpm.birt.osm.ui.icons.Icons;

public class OsmMapImageUi implements IReportItemImageProvider {
	private Image previous;
	
	@Override
	public Image getImage(ExtendedItemHandle handle) {

		DimensionValue width = (DimensionValue) handle.getProperty("width");
		DimensionValue height = (DimensionValue) handle.getProperty("height");

		if(previous == null) {
			previous = Activator.getDefault().getImageRegistry().get(Icons.OSM_MAP);
		}
		
		
		
		return resize(previous, convertToPx(width), convertToPx(height));
	}

	private int convertToPx(DimensionValue size) {
		try {
			if(size.getUnits().equals("px")) {
				return (int) size.getMeasure();
			}
			else if(size.getUnits().equals("pt")) {
				return ((int) (size.getMeasure() * 96)) * 72;
			}
			else if(size.getUnits().equals("in")) {
				return (int) (size.getMeasure() * 96);
			}
			else if(size.getUnits().equals("pc")) {
				return ((int) (size.getMeasure() * 96)) * 6;
			}
			else if(size.getUnits().equals("cm")) {
				return (int) (((int) (size.getMeasure() * 96)) * 2.54);
			}
			else if(size.getUnits().equals("mm")) {
				return ((int) (((int) (size.getMeasure() * 96)) * 2.54)) * 1000;
			}
		} catch (Exception e) {
			return 200;
		}
		return 200;
	}

	@Override
	public void disposeImage(ExtendedItemHandle handle, Image image) {
		// if (image != null && !image.isDisposed()) {
		// image.dispose();
		// }
	}

	private Image resize(Image image, int width, int height) {
		Image scaled = new Image(Display.getDefault(), width, height);
		GC gc = new GC(scaled);
		gc.setAntialias(SWT.ON);
		gc.setInterpolation(SWT.HIGH);
		gc.drawImage(image, 0, 0, image.getBounds().width, image.getBounds().height, 0, 0, width, height);
		gc.dispose();
		image.dispose(); // don't forget about me!
		previous = scaled;
		return scaled;
	}
}
