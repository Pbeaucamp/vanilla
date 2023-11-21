package bpm.sqldesigner.query.model.filter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import bpm.sqldesigner.query.model.Column;

public class ColumnFilters {

	private HashMap<Column, List<ColumnFilterWithOperation>> filters = new HashMap<Column, List<ColumnFilterWithOperation>>();
	private List<ColumnFilter> predefinedFilters;

	public List<ColumnFilterWithOperation> getFilters(Column c) {
		List<ColumnFilterWithOperation> list = filters.get(c);
		if (list == null)
			return new ArrayList<ColumnFilterWithOperation>();
		else
			return list;
	}

	public void putFilter(Column c, ColumnFilterWithOperation s) {
		List<ColumnFilterWithOperation> newList = getFilters(c);
		if (newList == null)
			newList = new ArrayList<ColumnFilterWithOperation>();
		findAndRemove(c, newList, s.getName());
		newList.add(s);
		filters.put(c, newList);
		c.setFiltred(true);
	}

	private boolean findAndRemove(Column c,
			List<ColumnFilterWithOperation> newList, String s) {
		Iterator<ColumnFilterWithOperation> it = newList.iterator();
		boolean found = false;
		while (it.hasNext() && !found) {
			ColumnFilterWithOperation f = it.next();
			found = f.getName().equals(s);
			if (found) {
				newList.remove(f);
				if (newList.size() == 0)
					c.setFiltred(false);
			}
		}

		return found;
	}

	public Column findColumn(String columnName) {
		Column c = null;

		Set<Column> keySet = filters.keySet();
		Iterator<Column> itKey = keySet.iterator();

		boolean found = false;

		while (itKey.hasNext() && !found) {
			c = itKey.next();
			found = c.getName().equals(columnName);
		}
		if (found)
			return c;
		return null;
	}

	public ColumnFilterWithOperation findFilter(Column c, String filterName) {
		ColumnFilterWithOperation cFilter = null;

		if (c != null) {
			List<ColumnFilterWithOperation> listFilters = getFilters(c);
			Iterator<ColumnFilterWithOperation> it = listFilters.iterator();

			boolean found = false;
			while (it.hasNext() && !found) {
				cFilter = it.next();
				found = cFilter.getName().equals(filterName);
			}
		}

		return cFilter;
	}

	public void remove(Column column,
			ColumnFilterWithOperation columnFilterWithOperation) {
		findAndRemove(column, getFilters(column), columnFilterWithOperation
				.getName());
	}

	public HashMap<Column, List<ColumnFilterWithOperation>> getAllFilters() {
		return filters;
	}

	public List<ColumnFilter> getPredefinedFilters() {
		return predefinedFilters;
	}

	public void setPredefinedFilters(List<ColumnFilter> predefinedFilters) {
		this.predefinedFilters = predefinedFilters;
	}

	public void removeAll() {
	}

	public void removeForColumn(Column c) {
		filters.remove(c);
	}
}
