package groupviewer.figure;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.GridData;
import org.eclipse.draw2d.GridLayout;

public class ContentFigure extends Figure {

	GridData centerData;

	public ContentFigure() {
		GridLayout layout = new GridLayout(3, true);
		layout.marginWidth = 10;
		layout.marginWidth = 10;
		layout.horizontalSpacing = 10;
		layout.verticalSpacing = 10;
		setLayoutManager(layout);
	}
}
