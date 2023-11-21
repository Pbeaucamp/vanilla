package bpm.fd.design.ui.editor.figures;

import java.awt.Point;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.ImageFigure;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Rectangle;

import bpm.fd.api.core.model.components.definition.IComponentDefinition;
import bpm.fd.api.core.model.components.definition.chart.ComponentChartDefinition;
import bpm.fd.design.ui.editor.figures.svg.SvgUtility;

public class ChartFigure extends Figure implements IComponentFigure {

	private ImageFigure fig;

	public ChartFigure(int nature) {
		setLayoutManager(new XYLayout());
	}

	public void update(IComponentDefinition def, Point preferedSIZE) {

		try {
			ImageFigure f = SvgUtility.createImage(((ComponentChartDefinition) def).getNature(), preferedSIZE);
			if(getChildren().contains(fig)) {
				remove(fig);
			}
			fig = f;
			add(fig, new Rectangle(0, 0, preferedSIZE.x, preferedSIZE.y));
		} catch(Exception e) {
		}

	}

}