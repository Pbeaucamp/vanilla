package bpm.gwt.commons.client.custom.v2;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;

/**
 * Composite containing data of type <T>
 * 
 * You can add {@link ValueChangeHandler} to monitore changes of values
 * 
 * @param <T>
 */
public abstract class CompositeData<T> extends Composite {

	private boolean isRequired;

	private List<ValueChangeHandler> handlers;

	public void setRequired(boolean isRequired) {
		this.isRequired = isRequired;
		if (getRequired() != null) {
			getRequired().setVisible(isRequired);
		}
	}

	public boolean isValid() {
		T value = getValue();
		return !isRequired || value != null;
	}

	public void addValueChangeHandler(ValueChangeHandler handler) {
		if (handlers == null) {
			handlers = new ArrayList<CompositeData.ValueChangeHandler>();
		}
		this.handlers.add(handler);
	}

	public void onValueChange() {
		if (handlers != null) {
			for (ValueChangeHandler handler : handlers) {
				handler.onValueChange();
			}
		}
	}

	public abstract T getValue();

	public abstract void setValue(T value);

	public abstract Label getRequired();
	
	public abstract void setEnabled(boolean enabled);

	/**
	 * Handler for data change
	 */
	public interface ValueChangeHandler {

		public void onValueChange();
	}

	/**
	 * Handler to register widget {@link CompositeData}
	 */
	public interface CompositeManagerHandler {

		public void registerWidget(CompositeData<?> widget);

		public void unregisterWidget(CompositeData<?> widget);
	}
}
