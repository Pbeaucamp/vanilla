package groupviewer.figure;

import groupviewer.Messages;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.GridData;
import org.eclipse.draw2d.GridLayout;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

public abstract class Item extends Figure {

	// Default Data
	private final static String DEFAULT_TITLE = Messages.Figure_Item_0;
	private static Image DEFAULT_ICO;
	private final int GRAD_PER_CENT = 20;
	private final static Color TITLE_COLOR = ColorConstants.titleForeground;
	private final static Color TITLE_COLOR_GRAD = ColorConstants.titleGradient;
	// Private Data
	private int titleHeight;
	private GridLayout layout;
	private Label _title;

	public Item() {
		super();
		DEFAULT_ICO = PlatformUI.getWorkbench().getSharedImages().getImage(
				ISharedImages.IMG_OBJ_ELEMENT);
		// Title
		_title = new Label();
		// Layout
		layout = new GridLayout();
		layout.marginHeight = 4;
		layout.marginWidth = 4;
		layout.verticalSpacing = 6;
		setLayoutManager(layout);
		// Add Section
		add(_title, new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING,
				GridData.VERTICAL_ALIGN_BEGINNING, false, false));

	}

	@Override
	public void validate() {
		initTitle();
		super.validate();
	}

	private void initTitle() {
		// Title Text
		if (getTitle() == null)
			_title.setText(DEFAULT_TITLE);
		else
			_title.setText(titleFormat(getTitle()));
		// Title Icon
		if (getTitleIcon() == null)
			_title.setIcon(DEFAULT_ICO);
		else
			_title.setIcon(getTitleIcon());
		// Define the title Height
		this.titleHeight = _title.getPreferredSize().height;
	}

	private String titleFormat(String title) {
		return title.trim().toUpperCase();
	}

	@Override
	public void paint(Graphics graphics) {
		super.paint(graphics);

		Rectangle dim = getBounds();
		int height = titleHeight + layout.marginHeight + layout.verticalSpacing
				- 3;
		int grad = height % GRAD_PER_CENT;
		Color oldBgColor = graphics.getBackgroundColor();

		graphics.setBackgroundColor(ColorConstants.titleGradient);
		graphics.fillRectangle(dim.x, dim.y, dim.width, height);
		graphics.setForegroundColor(TITLE_COLOR_GRAD);
		graphics.setBackgroundColor(TITLE_COLOR);
		graphics.fillGradient(dim.x, dim.y + (height - grad), dim.width, grad,
				true);
		
		graphics.setBackgroundColor(oldBgColor);
		graphics.setForegroundColor(ColorConstants.black);
		graphics.drawRectangle(dim.x, dim.y, dim.width - 1, height);

		graphics.drawImage(DEFAULT_ICO, dim.x + 5, dim.y + 5);
		graphics.drawString(getTitle().toUpperCase(), dim.x
				+ DEFAULT_ICO.getBounds().width + 10, dim.y + 5);
	}

	abstract String getTitle();

	abstract Image getTitleIcon();

}
