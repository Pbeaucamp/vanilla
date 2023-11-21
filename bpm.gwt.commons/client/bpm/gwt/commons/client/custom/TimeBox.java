package bpm.gwt.commons.client.custom;

import java.util.Date;

import bpm.gwt.commons.client.images.CommonImages;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.dom.client.MouseWheelEvent;
import com.google.gwt.event.dom.client.MouseWheelHandler;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class TimeBox extends Composite implements HasValueChangeHandlers<Long>, Focusable {

	private static final String STYLE_TIMEPICKER = "timePicker";
	private static final String STYLE_TIMEPICKER_ENTRY = "timePickerEntry";
	private static final String STYLE_TIMEPICKER_READONLY = "timePickerReadOnly";

	public static final long SECONDS = 1000;
	public static final long MINUTES = 60 * SECONDS;
	public static final long HOURS = 60 * MINUTES;
	
	private final ValueTextBox hoursBox;
	private final ValueTextBox minutesBox;

	public TimeBox() {
		this(new Date());
	}
	
	/**
	 * Default Constructor.
	 * 
	 * @param time
	 *            Hour that will show the widget
	 */
	public TimeBox(Date time) {
		int hour = Integer.parseInt(DateTimeFormat.getFormat("HH").format(time));
		int minutes = Integer.parseInt(DateTimeFormat.getFormat("mm").format(time));

		hoursBox = new ValueTextBox(hour, 0, 23);
		hoursBox.setMinDigits(2);
		hoursBox.setSteps(0);

		minutesBox = new ValueTextBox(minutes, 0, 59);
		minutesBox.setMinDigits(2);
		minutesBox.setSteps(0);

		Label separator = new Label(":");
		separator.getElement().getStyle().setMargin(-4, Unit.PX);

		hoursBox.setWidth("30px");
		hoursBox.getElement().getStyle().setMargin(3, Unit.PX);
		minutesBox.setWidth("30px");
		minutesBox.getElement().getStyle().setMargin(3, Unit.PX);

		hoursBox.setStyleName(getStyleTimePickerEntry());
		separator.setStyleName(getStyleTimePickerEntry());
		minutesBox.setStyleName(getStyleTimePickerEntry());

		HorizontalPanel timePanel = new HorizontalPanel();
		timePanel.setStyleName(getStyleTimePicker());
		timePanel.add(hoursBox);
		timePanel.add(separator);
		timePanel.add(minutesBox);
		timePanel.setCellVerticalAlignment(separator, HasVerticalAlignment.ALIGN_MIDDLE);

		VerticalPanel container = new VerticalPanel();
		container.add(timePanel);

		initWidget(container);

		hoursBox.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				// fireValueChange();
			}
		});
		hoursBox.addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				int keyCode = event.getNativeEvent().getKeyCode();

				int hour = getSelectedHours();
				int oldHour = hour;
				boolean stepKey = false;

				switch (keyCode) {
				case KeyCodes.KEY_UP:
					stepKey = true;
					hour++;
					break;
				case KeyCodes.KEY_DOWN:
					stepKey = true;
					hour--;
					break;
				}

				if (stepKey) {
					applyHourValue(oldHour, hour);
				}
			}
		});
		hoursBox.addMouseWheelHandler(new MouseWheelHandler() {
			@Override
			public void onMouseWheel(MouseWheelEvent event) {
				int hour = getSelectedHours();
				int oldHour = hour;
				if (event.isNorth()) {
					hour++;
				}
				else {
					hour--;
				}

				applyHourValue(oldHour, hour);
				event.preventDefault();
			}
		});

		minutesBox.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				// event.getValue();
				//
				// int oldHour = Integer.parseInt(hoursBox.getValue());

				// fireValueChange();
			}
		});
		minutesBox.addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				int keyCode = event.getNativeEvent().getKeyCode();

				int minutes = getSelectedMinutes();
				int oldMinutes = minutes;
				boolean stepKey = false;

				switch (keyCode) {
				case KeyCodes.KEY_UP:
					stepKey = true;
					minutes++;
					break;
				case KeyCodes.KEY_DOWN:
					stepKey = true;
					minutes--;
					break;
				}

				if (stepKey) {
					applyMinutesValue(oldMinutes, minutes);
				}
			}
		});
		minutesBox.addMouseWheelHandler(new MouseWheelHandler() {
			@Override
			public void onMouseWheel(MouseWheelEvent event) {
				int minutes = getSelectedMinutes();
				int oldMinutes = minutes;
				if (event.isNorth()) {
					minutes++;
				}
				else {
					minutes--;
				}
				applyMinutesValue(oldMinutes, minutes);
				event.preventDefault();
			}
		});

		// Trying to force that the whole value is selected when receiving the
		// focus
		hoursBox.addFocusHandler(new FocusHandler() {
			@Override
			public void onFocus(FocusEvent event) {
				hoursBox.setFocus(true);
				hoursBox.selectAll();
			}
		});
		minutesBox.addFocusHandler(new FocusHandler() {
			@Override
			public void onFocus(FocusEvent event) {
				minutesBox.setFocus(true);
				minutesBox.selectAll();
			}
		});
		// Making sure the cursor is always in the last position so the editing
		// works as expected
		hoursBox.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				hoursBox.setCursorPos(hoursBox.getText().length());
			}
		});
		minutesBox.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				minutesBox.setCursorPos(minutesBox.getText().length());
			}
		});
	}

	public void setValue(Date time) {
		int hours = Integer.parseInt(DateTimeFormat.getFormat("HH").format(time));
		int minutes = Integer.parseInt(DateTimeFormat.getFormat("mm").format(time));

		int oldHours = getSelectedHours();
		int oldMinutes = getSelectedMinutes();

		applyHourValue(oldHours, hours);
		applyMinutesValue(oldMinutes, minutes);
	}

	public int getSelectedHours() {
		return Integer.parseInt(hoursBox.getText());
	}

	public int getSelectedMinutes() {
		return Integer.parseInt(minutesBox.getText());
	}

	private void applyHourValue(int oldHours, int hours) {
		if (oldHours != hours) {
			if (hours >= 24) {
				hours = hours % 24;
			}
			else if(hours < 0) {
				hours = 23;
			}
			hoursBox.setValue(String.valueOf(hours));
		}
	}

	private void applyMinutesValue(int oldMinutes, int minutes) {
		if (oldMinutes != minutes) {
			if (minutes >= 60) {
				minutes = minutes % 60;
				
				int oldHours = getSelectedHours();
				int hours = oldHours + 1;
				
				applyHourValue(oldHours, hours);
			}
			else if (minutes < 0) {
				minutes = 59;
				
				int oldHours = getSelectedHours();
				int hours = oldHours - 1;
				
				applyHourValue(oldHours, hours);
			}

			minutesBox.setValue(String.valueOf(minutes));
		}
	}

	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Long> handler) {
		return addHandler(handler, ValueChangeEvent.getType());
	}

	@Override
	public int getTabIndex() {
		return hoursBox.getTabIndex();
	}

	@Override
	public void setAccessKey(char key) {
		hoursBox.setAccessKey(key);
	}

	@Override
	public void setFocus(boolean focused) {
		hoursBox.setFocus(focused);
		hoursBox.selectAll();
	}

	@Override
	public void setTabIndex(int index) {
		hoursBox.setTabIndex(index);
	}

	protected String getStyleTimePickerEntry() {
		return STYLE_TIMEPICKER_ENTRY;
	}

	protected String getStyleTimePicker() {
		return STYLE_TIMEPICKER;
	}

	protected String getStyleTimePickerReadOnly() {
		return STYLE_TIMEPICKER_READONLY;
	}

	protected Image getStyleTimePickerAM() {
		return new Image(CommonImages.INSTANCE.timepicker_AM());
	}

	protected Image getStyleTimePickerPM() {
		return new Image(CommonImages.INSTANCE.timepicker_PM());
	}

	public void setEnabled(boolean enabled) {
		hoursBox.setEnabled(enabled);
		minutesBox.setEnabled(enabled);
	}
}