package bpm.birt.wms.map.ui.wizard;

import org.eclipse.birt.report.model.api.DataSetHandle;
import org.eclipse.birt.report.model.api.ExtendedItemHandle;
import org.eclipse.jface.wizard.Wizard;

import bpm.birt.wms.map.core.reportitem.ColorRange;
import bpm.birt.wms.map.core.reportitem.WMSMapItem;

public class WMSMapWizard extends Wizard {
	
	private ExtendedItemHandle handle;
	private WMSMapItem item;
	private DataSetHandle dataset;
	
	private WMSDataPage pageData;
	
	public WMSMapWizard(WMSMapItem item, DataSetHandle dataset, ExtendedItemHandle handle) {
		this.item = item;
		this.dataset = dataset;
		this.handle = handle;
	}

	@Override
	public void addPages() {
		WMSDefinitionPage pageDef = new WMSDefinitionPage("Event parameters", item);
		pageDef.setDescription("Definition of the WMS Map");
		pageDef.setTitle("WMS Map Definition");
		addPage(pageDef);
		
		pageData = new WMSDataPage("Event parameters", item, dataset, handle);
		pageData.setDescription("Data selection for the WMS map");
		pageData.setTitle("WMS Map Data");
		addPage(pageData);
	}
	
	@Override
	public boolean performFinish() {
		
		String ranges = "";
		
		boolean first = true;
		for(ColorRange range : pageData.getColorRanges()) {
			if(first) {
				first = false;
			}
			else {
				ranges += ";";
			}
			ranges += range.getName() + "|" + range.getMin() + "|" + range.getMax() + "|" + range.getHex();
		}
		
		item.setProperty(WMSMapItem.P_COLOR_RANGE, ranges);
		
		return true;
	}
	
}
