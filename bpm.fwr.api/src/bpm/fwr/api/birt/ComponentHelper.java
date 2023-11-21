package bpm.fwr.api.birt;

import org.eclipse.birt.report.model.api.DesignElementHandle;
import org.eclipse.birt.report.model.api.ElementFactory;
import org.eclipse.birt.report.model.api.ReportDesignHandle;

import bpm.fwr.api.beans.components.BirtCommentComponents;
import bpm.fwr.api.beans.components.ChartComponent;
import bpm.fwr.api.beans.components.CrossComponent;
import bpm.fwr.api.beans.components.GridComponent;
import bpm.fwr.api.beans.components.IReportComponent;
import bpm.fwr.api.beans.components.ImageComponent;
import bpm.fwr.api.beans.components.TextHTMLComponent;
import bpm.fwr.api.beans.components.VanillaChartComponent;
import bpm.fwr.api.beans.components.VanillaMapComponent;

public abstract class ComponentHelper {

	public static DesignElementHandle createComponent(ReportDesignHandle designHandle, ElementFactory designFactory, String selectedLanguage, IReportComponent component, String outputFormat) throws Exception {
		if (component instanceof GridComponent) {
			return new GridComponentCreator().createComponent(designHandle, designFactory, selectedLanguage, (GridComponent)component, outputFormat);
		}
		else if (component instanceof CrossComponent) {
			return new CrossComponentCreator().createComponent(designHandle, designFactory, selectedLanguage, (CrossComponent)component, outputFormat);
		}
		else if (component instanceof ChartComponent) {
			return new ChartComponentCreator().createComponent(designHandle, designFactory, selectedLanguage, (ChartComponent)component, outputFormat);
		}
		else if (component instanceof VanillaMapComponent) {
			return new VanillaMapComponentCreator().createComponent(designHandle, designFactory, selectedLanguage, (VanillaMapComponent)component, outputFormat);
		}
		else if (component instanceof VanillaChartComponent) {
			return new VanillaChartComponentCreator().createComponent(designHandle, designFactory, selectedLanguage, (VanillaChartComponent)component, outputFormat);
		}
		else if (component instanceof BirtCommentComponents) {
			return new BirtCommentComponentCreator().createComponent(designHandle, designFactory, selectedLanguage, (BirtCommentComponents)component, outputFormat);
		}
		else if (component instanceof ImageComponent) {
			return new ImageComponentCreator().createComponent(designHandle, designFactory, selectedLanguage, (ImageComponent)component, outputFormat);
		}
		else if (component instanceof TextHTMLComponent) {
			return new TextHTMLComponentCreator().createComponent(designHandle, designFactory, selectedLanguage, (TextHTMLComponent)component, outputFormat);
		}
		throw new Exception("This component is not supported for now : " + component.getClass());
	}
}
