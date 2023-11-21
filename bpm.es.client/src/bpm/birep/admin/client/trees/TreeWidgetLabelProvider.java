package bpm.birep.admin.client.trees;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import bpm.vanilla.platform.core.beans.Widgets;

import adminbirep.Activator;
import adminbirep.icons.Icons;

public class TreeWidgetLabelProvider extends LabelProvider{
	
	@Override
	public String getText(Object obj) {
		return obj.toString();
	}
	
	@Override
	public Image getImage(Object obj) {
		ImageRegistry reg = Activator.getDefault().getImageRegistry();
		
//		if (obj instanceof TreeParent<?>){
//			String widgetName = ((TreeParent<?>) obj).getName();
//			if(widgetName == Widgets.NETVIBES){
//				return reg.get(Icons.NETVIBES);
//			}
//			else if(widgetName == Widgets.FLUX_RSS){
//				return reg.get(Icons.FLUXRSS);
//			}
//			else {
//				return reg.get(Icons.FOLDER); 
//			}
//		}
//		else if(obj instanceof TreeWidget){
//			return reg.get(Icons.WIDGET);
//		}
				
		return reg.get("default"); //$NON-NLS-1$
	}
}
