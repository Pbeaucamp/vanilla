package bpm.workflow.ui.gef.figure;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.GridData;
import org.eclipse.draw2d.GroupBoxBorder;
import org.eclipse.draw2d.ScrollPane;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Display;

public class FigurePool extends ScrollPane {
	private Figure content = new Figure();
	public static Color blue = new Color(Display.getDefault(), 225, 225, 255);
	public static Font f = new Font(Display.getCurrent(), "Arial", 10, SWT.BOLD); //$NON-NLS-1$

	public FigurePool() {
		XYLayout layout = new XYLayout();
		content.setLayoutManager(layout);

		getViewport().setContents(content);

		this.setBackgroundColor(blue);
		setView(content);
	}

	public void setConstraint(GridData gd) {
		getParent().setConstraint(this, gd);
	}

	public Figure getContent(int width) {
		Dimension d = content.getPreferredSize();
		d.width = width;
		content.setPreferredSize(d);
		return content;
	}

	public void setProcessName(String n) {
		GroupBoxBorder border = new GroupBoxBorder();
		border.setLabel(n);
		border.setFont(f);
		content.setBorder(border);
	}

}
