package groupviewer.tree;

public interface ITreeObject{

	public Object getData();

	public void setData(Object data);
	
	public void setParent(TreeParent parent);
	
	public TreeParent getParent();

	public void setName(String name);
	
	public String getName();
	
	public String getFullName();
	
	public String toString();
	
}