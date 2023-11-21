package bpm.birt.fusioncharts.ui.viewers;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import bpm.birt.fusioncharts.core.model.IChart;
import bpm.birt.fusioncharts.ui.Activator;
import bpm.birt.fusioncharts.ui.icons.Icons;

public class ChartTypeTableLabelProvider extends LabelProvider{
	@Override
	public Image getImage(Object obj) {
		ImageRegistry reg = Activator.getDefault().getImageRegistry();
		if(obj.toString().equals(IChart.COLUMN_NAME)){
			return reg.get(Icons.SIDE_BY_SIDE_COLUMN_16);		
		}
		else if(obj.toString().equals(IChart.BAR_NAME)){
			return reg.get(Icons.SIDE_BY_SIDE_BAR_16);		
		}
		else if(obj.toString().equals(IChart.LINE_NAME)){
			return reg.get(Icons.OVERLAY_LINE_16);		
		}
		else if(obj.toString().equals(IChart.PIE_NAME)){
			return reg.get(Icons.PIE_16);		
		}
		else if(obj.toString().equals(IChart.DOUGHNUT_NAME)){
			return reg.get(Icons.DOUGHNUT_16);		
		}
		else if(obj.toString().equals(IChart.RADAR_NAME)){
			return reg.get(Icons.RADAR_16);		
		}
		else if(obj.toString().equals(IChart.PARETO_NAME)){
			return reg.get(Icons.PARETO_16);		
		}
		else if(obj.toString().equals(IChart.DRAG_NODE_NAME)){
			return reg.get(Icons.PARETO_16);	
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
	 */
	@Override
	public String getText(Object element) {
		return super.getText(element);
	}
}
