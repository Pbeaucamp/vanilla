package bpm.sqldesigner.query.figure;

import org.eclipse.draw2d.ActionEvent;
import org.eclipse.draw2d.ActionListener;
import org.eclipse.draw2d.CheckBox;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.ImageFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.draw2d.geometry.Rectangle;

import bpm.sqldesigner.query.Activator;
import bpm.sqldesigner.query.figure.constants.Fonts;

public class ColumnFigure extends Figure {

	private static final int GAP_TWEAK = 55;
	private Label labelColumn = new Label();
	private Label labelAnchorLeft = new Label("X");

	private CheckBox toggle = new CheckBox();
	private boolean compteur = true;
	private int tableFigureWidth;
	private String name = "";
	private boolean key = false;
	private ImageFigure filter;;

	public static final String CHECKBOX_ACTION_ADD = "ColumnFigureCheckboxADD";
	public static final String CHECKBOX_ACTION_REMOVE = "ColumnFigureCheckboxREMOVE";

	public ColumnFigure(boolean b) {
		ToolbarLayout layout = new ToolbarLayout();
		layout.setVertical(false);
		setLayoutManager(layout);
		//setBorder(new LineBorder(1));

		add(labelAnchorLeft);
		labelAnchorLeft.setVisible(false);

		toggle.addActionListener(new CheckboxActionListener(this));
		toggle.setSelected(b);
		add(toggle);

		labelColumn.setForegroundColor(ColorConstants.black);
		add(labelColumn);
		labelColumn.setFont(Fonts.normalSimple);

		filter = new ImageFigure(Activator.getDefault().getImageRegistry().get(
				"filter"));
		add(filter);
		filter.setVisible(false);

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

		labelColumn.setText(name + " : " + type);

		this.key = key;
		if (key) {
			// labelColumn.setFont(Fonts.normalBold);
			labelColumn.setIcon(Activator.getDefault().getImageRegistry().get(
					"key"));
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

	protected class CheckboxActionListener implements ActionListener {
		private ColumnFigure c;

		public CheckboxActionListener(ColumnFigure c) {
			this.c = c;
		}

		public void actionPerformed(ActionEvent event) {
			CheckBox checkbox = (CheckBox) event.getSource();
			if (checkbox.isSelected())
				c.firePropertyChange(CHECKBOX_ACTION_ADD, event, false);
			else
				c.firePropertyChange(CHECKBOX_ACTION_REMOVE, event, false);
		}

	}

	public void setFiltred(boolean newValue) {
		filter.setVisible(newValue);
	};

	public void setSelected(boolean b) {
		toggle.setSelected(b);
		ActionEvent a = new ActionEvent(toggle);
		
		if (b){
			firePropertyChange(CHECKBOX_ACTION_ADD, a, false);
		}
		else{
			firePropertyChange(CHECKBOX_ACTION_REMOVE, a, false);
		}
	}
}