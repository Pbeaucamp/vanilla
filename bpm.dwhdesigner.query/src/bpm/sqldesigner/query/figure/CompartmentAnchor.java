package bpm.sqldesigner.query.figure;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.ToolbarLayout;

import bpm.sqldesigner.query.figure.constants.Fonts;

public class CompartmentAnchor extends Figure {
	public static final int ANCHOR_FIGURE_DEFWIDTH = 2;

	public CompartmentAnchor() {
		ToolbarLayout layout = new ToolbarLayout();
		layout.setStretchMinorAxis(true);
		layout.setSpacing(2);
		setLayoutManager(layout);
		//setBorder(new LineBorder(1));


	}

	public Figure addAnchor(int index) {
		Label f = new Label("X");
		f.setVisible(false);
		f.setLabelAlignment(PositionConstants.EAST);
		f.setFont(Fonts.anchorRight);
		add(f, index);
		return f;
	}

}