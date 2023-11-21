package bpm.fd.design.ui.editor.figures;

import java.awt.Point;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.ImageFigure;

import bpm.fd.api.core.model.components.definition.IComponentDefinition;
import bpm.fd.api.core.model.components.definition.chart.ChartNature;
import bpm.fd.api.core.model.components.definition.chart.ComponentChartDefinition;
import bpm.fd.api.core.model.components.definition.link.ComponentLink;
import bpm.fd.api.core.model.components.definition.text.LabelComponent;
import bpm.fd.design.ui.gef.figures.PictureHelper;

public class FigureBuilder {

	public static IFigure createFigure(IComponentDefinition def, Point preferedSize) {
		IFigure fig = null;
		if(def instanceof LabelComponent) {
			fig = createLabel(def, preferedSize);
		}
		if(def instanceof ComponentLink) {
			fig = createHyperlink(def, preferedSize);
		}
		if(def instanceof ComponentChartDefinition) {
			try {
				ChartNature nature = ((ComponentChartDefinition) def).getNature();
				fig = createChart(def, preferedSize, nature.getNature());

				System.out.println(nature.getNature());
			} catch(Throwable t) {
				t.printStackTrace();
			}

		}
		if(fig == null) {
			try {
				fig = new ImageFigure(PictureHelper.getFullSizePicture(def));
			} catch(Exception ex) {
				ex.printStackTrace();
			}
		}
		return fig;
	}

	private static IFigure createChart(IComponentDefinition def, Point preferedSize, int nature) {
		ChartFigure cF = new ChartFigure(nature);
		cF.update(def, preferedSize);
		return cF;
	}

	private static IFigure createLabel(IComponentDefinition def, Point preferedSize) {
		LabelFigure f = new LabelFigure();
		f.update(def, preferedSize);
		return f;
	}

	private static IFigure createHyperlink(IComponentDefinition def, Point preferedSize) {
		HyperlinkFigure f = new HyperlinkFigure();
		f.update(def, preferedSize);
		return f;
	}
}
