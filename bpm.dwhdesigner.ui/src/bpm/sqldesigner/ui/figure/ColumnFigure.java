package bpm.sqldesigner.ui.figure;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.draw2d.geometry.Rectangle;

import bpm.sqldesigner.ui.Activator;
import bpm.sqldesigner.ui.figure.constants.Fonts;

public class ColumnFigure extends Figure {

	private static final int GAP_TWEAK = 20;
	private Label labelColumn = new Label();
	private Label labelAnchorLeft = new Label("X"); //$NON-NLS-1$

	private boolean compteur = true;
	private int tableFigureWidth;
	private String name = ""; //$NON-NLS-1$
	private boolean key = false;

	public static final String CHECKBOX_ACTION_ADD = "ColumnFigureCheckboxADD"; //$NON-NLS-1$
	public static final String CHECKBOX_ACTION_REMOVE = "ColumnFigureCheckboxREMOVE"; //$NON-NLS-1$

	public ColumnFigure() {
		ToolbarLayout layout = new ToolbarLayout();
		layout.setVertical(false);
		setLayoutManager(layout);

		add(labelAnchorLeft);
		labelAnchorLeft.setVisible(false);

		labelColumn.setForegroundColor(ColorConstants.black);
		add(labelColumn);
		labelColumn.setFont(Fonts.normalSimple);

		setOpaque(true);
	}

	public void setLayout(Rectangle rect) {
		setBounds(rect);
	}

	public void setLabelColumn(String name, String type, boolean key) {

		if (compteur) {
			tableFigureWidth = ((TableFigure) (getParent().getParent()
					.getParent())).getCurrentWidth();
			this.name = name;
			compteur = false;
		}

		labelColumn.setText(name + " : " + type); //$NON-NLS-1$

		this.key = key;
		if (key) {
			labelColumn.setIcon(Activator.getDefault().getImageRegistry().get(
					"key")); //$NON-NLS-1$
		}

		if (((CompartmentFigure) getParent()).getCurrentWidth() < getWidth()) {
			((ToolbarLayout) getParent().getParent().getLayoutManager())
					.setSpacing(tableFigureWidth - GAP_TWEAK - getWidth());
			((CompartmentFigure) getParent()).setCurrentWidth(getWidth());
		}
	}

	public int getWidth() {
		int w = labelColumn.getTextBounds().width;
		if (key)
			w = w + 16;
		return w;
	}

	public Figure getAnchorLeft() {
		return labelAnchorLeft;
	}

	public String getName() {
		return name;
	}

	public void toRed() {
		labelColumn.setForegroundColor(ColorConstants.red);
	}

	public void toBlack() {
		labelColumn.setForegroundColor(ColorConstants.black);

	}


}