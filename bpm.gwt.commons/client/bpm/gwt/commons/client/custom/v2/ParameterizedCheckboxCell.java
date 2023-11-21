package bpm.gwt.commons.client.custom.v2;

import java.util.Objects;

import com.google.gwt.cell.client.AbstractEditableCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;

import static com.google.gwt.dom.client.BrowserEvents.CLICK;
import static com.google.gwt.dom.client.BrowserEvents.KEYDOWN;

public class ParameterizedCheckboxCell extends AbstractEditableCell<Boolean, Boolean> {

	  /**
	   * An html string representation of a checked input box.
	   */
	  private static final SafeHtml INPUT_CHECKED = SafeHtmlUtils.fromSafeConstant("<input type=\"checkbox\" tabindex=\"-1\" checked/>");

	  /**
	   * An html string representation of an unchecked input box.
	   */
	  private static final SafeHtml INPUT_UNCHECKED = SafeHtmlUtils.fromSafeConstant("<input type=\"checkbox\" tabindex=\"-1\"/>");

	public ParameterizedCheckboxCell() {
		super(KEYDOWN, CLICK);
	}

	@Override
	public void render(Context context, Boolean value, SafeHtmlBuilder sb) {
		// Get the view data.
		Object key = context.getKey();
		Boolean viewData = getViewData(key);
		if (viewData != null && viewData.equals(value)) {
			clearViewData(key);
			viewData = null;
		}

		if (value != null && ((viewData != null) ? viewData : value)) {
			sb.append(INPUT_CHECKED);
		}
		else {
			sb.append(INPUT_UNCHECKED);
		}
	}

	@Override
	public void onBrowserEvent(Context context, Element parent, Boolean value, NativeEvent event, ValueUpdater<Boolean> valueUpdater) {
		final String type = event.getType();
		final boolean enterPressed = KEYDOWN.equals(type) && event.getKeyCode() == KeyCodes.KEY_ENTER;
		if (CLICK.equals(type) || enterPressed) {
			InputElement input = parent.getFirstChild().cast();
			Boolean isChecked = input.isChecked();

			if (enterPressed) {
				isChecked = !isChecked;
				input.setChecked(isChecked);
			}

			if (Objects.equals(value, isChecked)) {
				clearViewData(context.getKey());
			}
			else {
				setViewData(context.getKey(), isChecked);
			}
			valueUpdater.update(isChecked);
		}
	}

	@Override
	public boolean isEditing(Context context, Element parent, Boolean value) {
		return false;
	}

}