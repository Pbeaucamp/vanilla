package bpm.forms.core.runtime;

/**
 * store the submited datas for a specific IFormInstance Field 
 * @author ludo
 *
 */
public interface IFormInstanceFieldState {

	public long getFormInstanceId();
	
	public long getFormFieldMappingId();
	
	public String getValue();
	
	public long getId();

	public void setFormFieldMappingId(long id);

	public void setFormInstanceId(long id);

	public void setValue(String string);

	public void setValidated(int i);

	public int getValidated();
	
	public void setId(long id);
	
}
