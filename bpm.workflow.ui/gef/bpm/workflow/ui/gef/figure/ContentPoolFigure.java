package bpm.workflow.ui.gef.figure;

import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.draw2d.GridLayout;

public class ContentPoolFigure extends FreeformLayer {

	public ContentPoolFigure() {
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		setLayoutManager(layout);
	}

}
