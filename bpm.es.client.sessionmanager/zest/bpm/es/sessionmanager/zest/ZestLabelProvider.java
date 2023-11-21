package bpm.es.sessionmanager.zest;

import org.eclipse.draw2d.IFigure;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.zest.core.viewers.IConnectionStyleProvider;
import org.eclipse.zest.core.viewers.IEntityStyleProvider;
import org.eclipse.zest.core.viewers.IFigureProvider;

import bpm.es.sessionmanager.api.server.VanillaServer;

public class ZestLabelProvider implements IFigureProvider, ILabelProvider {

	public IFigure getFigure(Object element) {
		if (element instanceof VanillaServer) {
			IFigure figure = new ServerFigure((VanillaServer)element);
			return figure;
		}
		else {
			return null;
		}
	}

	public Image getImage(Object element) {
		
		return null;
	}

	public String getText(Object element) {
		return "ahahah"; //$NON-NLS-1$
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
