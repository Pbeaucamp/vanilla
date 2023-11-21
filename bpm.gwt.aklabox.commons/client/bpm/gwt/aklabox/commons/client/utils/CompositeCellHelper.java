package bpm.gwt.aklabox.commons.client.utils;

import java.util.ArrayList;
import java.util.List;

import bpm.gwt.aklabox.commons.client.panels.HasActionCell;

import com.google.gwt.cell.client.CompositeCell;
import com.google.gwt.cell.client.HasCell;

public class CompositeCellHelper<T> {

	private CompositeCell<T> cell;

	@SafeVarargs
	public CompositeCellHelper(HasActionCell<T>... cells) {
		List<HasCell<T, ?>> list = buildList(cells);
		cell = new CompositeCell<T>(list);
	}

	public CompositeCell<T> getCell() {
		return cell;
	}

	private List<HasCell<T, ?>> buildList(HasActionCell<T>[] cells) {
		List<HasCell<T, ?>> list = new ArrayList<HasCell<T, ?>>();
		if (cells != null) {
			for (HasActionCell<T> cell : cells) {
				list.add(cell);
			}
		}
		return list;
	}
}
