package bpm.fwr.api.birt;

import org.eclipse.birt.report.model.api.DesignElementHandle;
import org.eclipse.birt.report.model.api.ElementFactory;
import org.eclipse.birt.report.model.api.ReportDesignHandle;

import bpm.fwr.api.beans.components.IReportComponent;

public interface IComponentCreator<T extends IReportComponent> {

	public DesignElementHandle createComponent(ReportDesignHandle designHandle, ElementFactory designFactory, String selectedLanguage, T component, String outputFormat) throws Exception;
}
