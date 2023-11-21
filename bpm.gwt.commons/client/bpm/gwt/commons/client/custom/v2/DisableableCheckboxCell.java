package bpm.gwt.commons.client.custom.v2;

import com.google.gwt.cell.client.AbstractEditableCell;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;

/**
 * A implementation of the {@link AbstractEditableCell} with a checkbox which can be enable/disable.
 *
 */
public class DisableableCheckboxCell extends AbstractEditableCell<CheckState, CheckState> {

	/**
	 * An html string representation of a checked input box.
	 */
	private static final SafeHtml INPUT_CHECKED = SafeHtmlUtils.fromSafeConstant("<input type=\"checkbox\" tabindex=\"-1\" checked/>");

	/**
	 * An html string representation of an unchecked input box.
	 */
	private static final SafeHtml INPUT_UNCHECKED = SafeHtmlUtils.fromSafeConstant("<input type=\"checkbox\" tabindex=\"-1\"/>");

	private static final SafeHtml INPUT_CHECKED_DISABLED = SafeHtmlUtils.fromSafeConstant("<input type=\"checkbox\" tabindex=\"-1\" checked disabled=\"disabled\"/>");

	private static final SafeHtml INPUT_UNCHECKED_DISABLED = SafeHtmlUtils.fromSafeConstant("<input type=\"checkbox\" tabindex=\"-1\" disabled=\"disabled\"/>");

	private final boolean dependsOnSelection;
	private final boolean handlesSelection;

	/**
	 * Construct a new {@link CheckboxCell}.
	 */
	public DisableableCheckboxCell() {
		this(false);
	}

	/**
	 * Construct a new {@link CheckboxCell} that optionally controls selection.
	 * 
	 * @param isSelectBox
	 *            true if the cell controls the selection state
	 * @deprecated use {@link #CheckboxCell(boolean, boolean)} instead
	 */
	@Deprecated
	public DisableableCheckboxCell(boolean isSelectBox) {
		this(isSelectBox, isSelectBox);
	}

	/**
	 * Construct a new {@link CheckboxCell} that optionally controls selection.
	 * 
	 * @param dependsOnSelection
	 *            true if the cell depends on the selection state
	 * @param handlesSelection
	 *            true if the cell modifies the selection state
	 */
	public DisableableCheckboxCell(boolean dependsOnSelection, boolean handlesSelection) {
		super(BrowserEvents.CHANGE, BrowserEvents.KEYDOWN);
		this.dependsOnSelection = dependsOnSelection;
		this.handlesSelection = handlesSelection;
	}

	@Override
	public boolean dependsOnSelection() {
		return dependsOnSelection;
	}

	@Override
	public boolean handlesSelection() {
		return handlesSelection;
	}

	@Override
	public boolean isEditing(Context context, Element parent, CheckState value) {
		// A checkbox is never in "edit mode". There is no intermediate state
		// between checked and unchecked.
		return false;
	}

	@Override
	public void onBrowserEvent(Context context, Element parent, CheckState value, NativeEvent event, ValueUpdater<CheckState> valueUpdater) {
		String type = event.getType();

		boolean enterPressed = BrowserEvents.KEYDOWN.equals(type) && event.getKeyCode() == KeyCodes.KEY_ENTER;
		if (BrowserEvents.CHANGE.equals(type) || enterPressed) {
			InputElement input = parent.getFirstChild().cast();
			Boolean isChecked = input.isChecked();

			/*
			 * Toggle the value if the enter key was pressed and the cell
			 * handles selection or doesn't depend on selection. If the cell
			 * depends on selection but doesn't handle selection, then ignore
			 * the enter key and let the SelectionEventManager determine which
			 * keys will trigger a change.
			 */
			if (enterPressed && (handlesSelection() || !dependsOnSelection())) {
				isChecked = !isChecked;
				input.setChecked(isChecked);
			}

			/*
			 * Save the new value. However, if the cell depends on the
			 * selection, then do not save the value because we can get into an
			 * inconsistent state.
			 */
			if (value.isCheck() != isChecked && !dependsOnSelection()) {
				value.setCheck(isChecked);
				setViewData(context.getKey(), value);
			}
			else {
				clearViewData(context.getKey());
			}

			if (valueUpdater != null) {
				valueUpdater.update(value);
			}
		}
	}

	@Override
	public void render(Context context, CheckState value, SafeHtmlBuilder sb) {
		// Get the view data.
		Object key = context.getKey();
		CheckState viewData = getViewData(key);
		if (viewData != null && viewData.equals(value)) {
			clearViewData(key);
			viewData = null;
		}

		// CheckState relevantValue = viewData != null ? viewData : value;
		boolean checked = value.isCheck();
		boolean enabled = value.isEnable();

		if (checked && !enabled) {
			sb.append(INPUT_CHECKED_DISABLED);
		}
		else if (!checked && !enabled) {
			sb.append(INPUT_UNCHECKED_DISABLED);
		}
		else if (checked && enabled) {
			sb.append(INPUT_CHECKED);
		}
		else if (!checked && enabled) {
			sb.append(INPUT_UNCHECKED);
		}
	}
}