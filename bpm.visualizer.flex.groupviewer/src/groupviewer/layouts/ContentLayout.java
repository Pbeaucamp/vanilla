package groupviewer.layouts;

import org.eclipse.draw2d.ToolbarLayout;

public class ContentLayout extends ToolbarLayout{
	 static final int MAJOR_SPACING = 10;
	
	
	public ContentLayout()
	{
		setMinorAlignment(ALIGN_TOPLEFT);
		setSpacing(MAJOR_SPACING);
		setVertical(false);
	}
}
