package bpm.gwt.aklabox.commons.client.panels;

import bpm.gwt.aklabox.commons.client.utils.GridButtonImageCell;

import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.cell.client.ActionCell.Delegate;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.HasCell;
import com.google.gwt.resources.client.ImageResource;

public class HasActionCell<T> implements HasCell<T, T> {
	
	private ActionCell<T> cell;

	public HasActionCell(ImageResource resource, Delegate<T> delegate) {
		this(resource, "", null, delegate);
	}
	
	public HasActionCell(ImageResource resource, String title, Delegate<T> delegate) {
		this(resource, title, null, delegate);
	}

	public HasActionCell(ImageResource image, String title, String styleName, Delegate<T> delegate) {
		this(new GridButtonImageCell<T>(image, title, styleName, delegate));
	}
	
	public HasActionCell(ActionCell<T> cell) {
		this.cell = cell;
	}

	@Override
	public Cell<T> getCell() {
		return cell;
	}

	@Override
	public FieldUpdater<T, T> getFieldUpdater() {
		return null;
	}

	@Override
	public T getValue(T object) {
		return object;
	}
}