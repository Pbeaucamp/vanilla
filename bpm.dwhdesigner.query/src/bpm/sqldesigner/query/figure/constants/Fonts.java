package bpm.sqldesigner.query.figure.constants;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Display;

public abstract class Fonts {

	public final static Font normalBold = new Font(Display.getCurrent(), "Arial",
			10, SWT.BOLD);
	public final static Font normalSimple = new Font(Display.getCurrent(), "Arial",
			10, SWT.NORMAL);
	public final static Font bigSimple = new Font(Display.getCurrent(), "Arial",
			12, SWT.NORMAL);
	public final static Font anchorRight = new Font(Display.getCurrent(), "Arial",
			10, SWT.NORMAL);

}
