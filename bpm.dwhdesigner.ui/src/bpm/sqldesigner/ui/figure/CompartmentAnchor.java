package bpm.sqldesigner.ui.figure;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.draw2d.geometry.Rectangle;

import bpm.sqldesigner.ui.figure.constants.Fonts;


public class CompartmentAnchor extends Figure {
	public static final int ANCHOR_FIGURE_DEFWIDTH = 2;

	public CompartmentAnchor() {
		ToolbarLayout layout = new ToolbarLayout();
		layout.setStretchMinorAxis(true);
		layout.setSpacing(2);
		setLayoutManager(layout);


	}

	public Figure addAnchor(int index) {
		Label f = new Label("X"); //$NON-NLS-1$
		f.setVisible(false);
		f.setLabelAlignment(PositionConstants.EAST);
		f.setFont(Fonts.anchorRight);
		add(f, index);
		layout();
		this.repaint();
		setBounds(new Rectangle(10,10,30,30));

		return f;
	}

}