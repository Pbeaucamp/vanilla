package bpm.fd.design.ui.properties.model.editors;

import org.eclipse.swt.widgets.Composite;

import bpm.fd.api.core.model.components.definition.report.ComponentKpi;
import bpm.fd.api.core.model.components.definition.report.ComponentReport;
import bpm.fd.api.core.model.components.definition.report.ReportOptions;

public class ReportEditor extends VanillaObjectEditor{

	public ReportEditor(Composite parent) {
		super(parent);
		
	}

	private ReportOptions getOptions(){
		try {
			return (ReportOptions)((ComponentReport)getComponentDefinition()).getOptions(ReportOptions.class);
		} catch(Exception e) {
			return (ReportOptions)((ComponentKpi)getComponentDefinition()).getOptions(ReportOptions.class);
		}
	}
	@Override
	protected int getHeight() {
		
		return getOptions().getHeight();
	}

	@Override
	protected int getWidth() {
		return getOptions().getWidth();
	}

	@Override
	protected void setHeight(Integer height) {
		getOptions().setHeight(height);
		
	}

	@Override
	protected void setItemId(Integer id) {
		try {
			((ComponentReport)getComponentDefinition()).setDirectoryItemId(id);
		} catch(Exception e) {
			((ComponentKpi)getComponentDefinition()).setDirectoryItemId(id);
		}
		
	}

	@Override
	protected void setWidth(Integer width) {
		getOptions().setWidth(width);
		
	}

}
