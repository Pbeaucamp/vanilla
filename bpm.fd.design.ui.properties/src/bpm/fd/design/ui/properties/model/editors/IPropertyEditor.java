package bpm.fd.design.ui.properties.model.editors;

public interface IPropertyEditor {
	public void filter(String propertyName);
	public boolean next(String text);
}
