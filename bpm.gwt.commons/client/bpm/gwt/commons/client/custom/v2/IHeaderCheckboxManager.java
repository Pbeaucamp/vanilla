package bpm.gwt.commons.client.custom.v2;

/**
 * Manager for datagrid with {@link HeaderCheckboxCell}
 * 
 * @param <T>
 */
public interface IHeaderCheckboxManager<T> {

	public void update(T object, Boolean value);
	
	public void refreshGrid();
}
