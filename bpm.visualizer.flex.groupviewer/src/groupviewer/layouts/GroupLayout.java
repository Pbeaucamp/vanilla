package groupviewer.layouts;

import org.eclipse.draw2d.GridLayout;
import org.eclipse.draw2d.IFigure;

public class GroupLayout extends GridLayout{
	
	final static int H_SPACING = 5;
	
	
	public GroupLayout()
	{
		super(1,true);
		horizontalSpacing = H_SPACING;
		marginHeight = 2;
		marginWidth = 2;
	}
	
	@Override
	public void layout(IFigure container) {
		super.layout(container);
	}
}
