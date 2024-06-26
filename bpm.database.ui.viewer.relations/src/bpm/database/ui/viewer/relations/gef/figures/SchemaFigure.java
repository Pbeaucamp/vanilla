package bpm.database.ui.viewer.relations.gef.figures;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Rectangle;

public class SchemaFigure extends Figure {
	
	private Label labelName = new Label();
	private XYLayout layout;

	
	public SchemaFigure() {
		layout = new XYLayout();
		setLayoutManager(layout);

		labelName.setForegroundColor(ColorConstants.blue);
		add(labelName);
		setConstraint(labelName, new Rectangle(5, 5, -1, -1));
		setForegroundColor(ColorConstants.black);
		setBorder(new LineBorder(1));
		
		
	}

	public void setLayout(Rectangle rect) {
		setBounds(rect);
	}

	public void setName(String text) {
		labelName.setText(text);
	}

}