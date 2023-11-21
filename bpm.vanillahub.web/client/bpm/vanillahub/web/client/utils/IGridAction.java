package bpm.vanillahub.web.client.utils;

public interface IGridAction<T> {

	public enum TypeAction {
		EDIT,
		DELETE
	}
	
	public void action(T selectedItem, TypeAction action);
}
