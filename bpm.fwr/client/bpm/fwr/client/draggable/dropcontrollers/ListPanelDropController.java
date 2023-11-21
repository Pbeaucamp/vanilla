package bpm.fwr.client.draggable.dropcontrollers;

import bpm.fwr.client.draggable.widgets.DraggableColumn;
import bpm.fwr.client.utils.DataStreamType;
import bpm.fwr.client.utils.SizeComponentConstants;
import bpm.fwr.client.widgets.ListCell;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.VetoDragException;
import com.allen_sauer.gwt.dnd.client.drop.AbstractInsertPanelDropController;
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

public class ListPanelDropController extends AbstractInsertPanelDropController {
	private ListCell listCell;

	/**
	 * Our drop target.
	 */
	protected final InsertPanel dropTarget;
	private int dropIndex;
	private Widget positioner = null;

	private boolean automaticGroupingList;

	/**
	 * Label for IE quirks mode workaround.
	 */
	private static final Label DUMMY_LABEL_IE_QUIRKS_MODE_OFFSET_HEIGHT = new Label("x");

	public ListPanelDropController(HorizontalPanel dropTarget, ListCell listCell, boolean automaticGroupingList) {
		super(dropTarget);
		this.dropTarget = dropTarget;
		this.listCell = listCell;
		this.automaticGroupingList = automaticGroupingList;
	}

	private boolean checkDrop(DragContext context) {
		return !automaticGroupingList || (context.selectedWidgets != null && !context.selectedWidgets.isEmpty() && context.selectedWidgets.get(0) instanceof DraggableColumn && ((DraggableColumn) context.selectedWidgets.get(0)).getColumn().isMeasure());
	}

	@Override
	public void onDrop(DragContext context) {
		assert dropIndex != -1 : "Should not happen after onPreviewDrop did not veto";
		for (Widget widget : context.selectedWidgets) {
			listCell.manageWidget(widget, dropIndex);
			dropIndex = dropTarget.getWidgetIndex(widget) + 1;
		}
	}

	@Override
	public void onEnter(DragContext context) {
		if (checkDrop(context)) {
			((Panel) dropTarget).addStyleName(DragClientBundle.INSTANCE.css().dropTargetEngage());
			positioner = newPositioner(context);
			int targetIndex = DOMUtil.findIntersect(dropTarget, new CoordinateLocation(context.mouseX, context.mouseY), getLocationWidgetComparator());
			dropTarget.insert(positioner, targetIndex);
		}
	}

	@Override
	public void onLeave(DragContext context) {
		if (checkDrop(context)) {
			positioner.removeFromParent();
			positioner = null;
			((Panel) dropTarget).removeStyleName(DragClientBundle.INSTANCE.css().dropTargetEngage());
		}
	}

	@Override
	public void onMove(DragContext context) {
		if (checkDrop(context)) {
			int targetIndex = DOMUtil.findIntersect(dropTarget, new CoordinateLocation(context.mouseX, context.mouseY), getLocationWidgetComparator());

			// check that positioner not already in the correct location
			int positionerIndex = dropTarget.getWidgetIndex(positioner);

			if (positionerIndex != targetIndex && (positionerIndex != targetIndex - 1 || targetIndex == 0)) {
				if (positionerIndex == 0 && dropTarget.getWidgetCount() == 1) {
					// do nothing, the positioner is the only widget
				}
				else if (targetIndex == -1) {
					// outside drop target, so remove positioner to indicate a
					// drop will not happen
					positioner.removeFromParent();
				}
				else {
					dropTarget.insert(positioner, targetIndex);
				}
			}
		}
	}

	@Override
	public void onPreviewDrop(DragContext context) throws VetoDragException {
		if (checkDrop(context)) {
			dropIndex = dropTarget.getWidgetIndex(positioner);
			if (dropIndex == -1) {
				throw new VetoDragException();
			}
		}
	}

	@Override
	protected Widget newPositioner(DragContext context) {
		// Use two widgets so that setPixelSize() consistently affects
		// dimensions
		// excluding positioner border in quirks and strict modes
		SimplePanel outer = new SimplePanel();
		outer.addStyleName(DragClientBundle.INSTANCE.css().positioner());
		// outer.addStyleName("testOuter");

		// place off screen for border calculation
		RootPanel.get().add(outer, -500, -500);

		// Ensure IE quirks mode returns valid outer.offsetHeight, and thus
		// valid
		// DOMUtil.getVerticalBorders(outer)
		outer.setWidget(DUMMY_LABEL_IE_QUIRKS_MODE_OFFSET_HEIGHT);

		int width = 100;
		int height = SizeComponentConstants.SIZE_LIST;

		SimplePanel inner = new SimplePanel();
		inner.setPixelSize((width / 2) - DOMUtil.getHorizontalBorders(outer), height - DOMUtil.getVerticalBorders(outer));

		outer.setWidget(inner);

		return outer;
	}

	@Override
	protected LocationWidgetComparator getLocationWidgetComparator() {
		return LocationWidgetComparator.RIGHT_HALF_COMPARATOR;
	}
}
