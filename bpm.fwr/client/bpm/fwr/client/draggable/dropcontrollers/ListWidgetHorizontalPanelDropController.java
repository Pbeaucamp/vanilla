package bpm.fwr.client.draggable.dropcontrollers;

import bpm.fwr.client.widgets.ListCell;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.VetoDragException;
import com.allen_sauer.gwt.dnd.client.drop.AbstractPositioningDropController;
import com.allen_sauer.gwt.dnd.client.drop.DropController;
import com.allen_sauer.gwt.dnd.client.drop.FlowPanelDropController;
import com.allen_sauer.gwt.dnd.client.util.CoordinateLocation;
import com.allen_sauer.gwt.dnd.client.util.DOMUtil;
import com.allen_sauer.gwt.dnd.client.util.DragClientBundle;
import com.allen_sauer.gwt.dnd.client.util.LocationWidgetComparator;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.InsertPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * A {@link DropController} for instances of {@link HorizontalPanel}.
 */
public class ListWidgetHorizontalPanelDropController extends AbstractPositioningDropController{

	/**
	* Label for IE quirks mode workaround.
	*/
	private static final Label DUMMY_LABEL_IE_QUIRKS_MODE_OFFSET_HEIGHT = new Label("x");

	/**
	* Our drop target.
	*/
	protected final InsertPanel dropTarget;

	private int dropIndex;

	private Widget positioner = null;
	  
	private ListCell cell;

	/**
	* @see FlowPanelDropController#FlowPanelDropController(com.google.gwt.user.client.ui.FlowPanel)
	* 
	* @param dropTarget the insert panel drop target
	*/
	public ListWidgetHorizontalPanelDropController(ListCell cell, HorizontalPanel dropTarget) {
		super((Panel) dropTarget);
		this.cell = cell;
		this.dropTarget = dropTarget;
	}

	@Override
	public void onDrop(DragContext context) {
		assert dropIndex != -1 : "Should not happen after onPreviewDrop did not veto";
		for (Widget widget : context.selectedWidgets) {
			dropTarget.insert(widget, dropIndex);
	    	cell.switchWidget(widget, dropIndex);
			// Works with and without drag proxy
			dropIndex = dropTarget.getWidgetIndex(widget) + 1;
		}
		super.onDrop(context);
	}

	@Override
	public void onEnter(DragContext context) {
		super.onEnter(context);
		positioner = newPositioner(context);
		int targetIndex = DOMUtil.findIntersect(dropTarget, new CoordinateLocation(context.mouseX,
				context.mouseY), getLocationWidgetComparator());
		dropTarget.insert(positioner, targetIndex);
	}

	@Override
	public void onLeave(DragContext context) {
		positioner.removeFromParent();
		positioner = null;
		super.onLeave(context);
	}

	@Override
	public void onMove(DragContext context) {
		super.onMove(context);
		int targetIndex = DOMUtil.findIntersect(dropTarget, new CoordinateLocation(context.mouseX,
				context.mouseY), getLocationWidgetComparator());

		// check that positioner not already in the correct location
		int positionerIndex = dropTarget.getWidgetIndex(positioner);
	
		if (positionerIndex != targetIndex && (positionerIndex != targetIndex - 1 || targetIndex == 0)) {
			if (positionerIndex == 0 && dropTarget.getWidgetCount() == 1) {
				// do nothing, the positioner is the only widget
			} else if (targetIndex == -1) {
				// outside drop target, so remove positioner to indicate a drop will not happen
				positioner.removeFromParent();
			} else {
				dropTarget.insert(positioner, targetIndex);
			}
		}
	}

	@Override
	public void onPreviewDrop(DragContext context) throws VetoDragException {
		dropIndex = dropTarget.getWidgetIndex(positioner);
		if (dropIndex == -1) {
			throw new VetoDragException();
		}
		super.onPreviewDrop(context);
	}

	protected LocationWidgetComparator getLocationWidgetComparator() {
		return LocationWidgetComparator.RIGHT_HALF_COMPARATOR;
	}

	protected Widget newPositioner(DragContext context) {
		// Use two widgets so that setPixelSize() consistently affects dimensions
		// excluding positioner border in quirks and strict modes
		SimplePanel outer = new SimplePanel();
		outer.addStyleName(DragClientBundle.INSTANCE.css().positioner());

		// place off screen for border calculation
		RootPanel.get().add(outer, -500, -500);

		// Ensure IE quirks mode returns valid outer.offsetHeight, and thus valid
		// DOMUtil.getVerticalBorders(outer)
		outer.setWidget(DUMMY_LABEL_IE_QUIRKS_MODE_OFFSET_HEIGHT);

		int width = 0;
		int height = 0;
		for (Widget widget : context.selectedWidgets) {
			width += widget.getOffsetWidth();
			height = Math.max(height, widget.getOffsetHeight());
		}

		SimplePanel inner = new SimplePanel();
		inner.setPixelSize(width - DOMUtil.getHorizontalBorders(outer), height
				- DOMUtil.getVerticalBorders(outer));

		outer.setWidget(inner);

		return outer;
	}
}
