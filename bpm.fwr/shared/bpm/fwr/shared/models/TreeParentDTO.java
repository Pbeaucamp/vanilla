package bpm.fwr.shared.models;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;


public class TreeParentDTO extends TreeObjectDTO implements IsSerializable{
	protected List<TreeObjectDTO> children;
	
	private String[][] availableGroups;
	private String comment = "";
	private String dateCreation = "" ;

	public TreeParentDTO() {
		super("");
		children = new ArrayList<TreeObjectDTO>();
	}
	
	public TreeParentDTO(String name) {
		super(name);
		children = new ArrayList<TreeObjectDTO>();
	}
	
	public void addChild(TreeObjectDTO child) {
		if (child == null)
			return;
		children.add(child);
		child.setParent(this);
	}
	public void removeChild(TreeObjectDTO child) {
		children.remove(child);
		child.setParent(null);
	}
	
	public Object[] getChildren() {
		return (Object[]) children.toArray(new Object[children.size()]);
	}
	public boolean hasChildren() {
		return children.size()>0;
	}
	public boolean isChild(Object obj) {
		for (int i = 0; i < children.size(); i++) {
			if (children.get(i) instanceof TreeParentDTO && obj instanceof TreeParentDTO) {
				if (children.get(i).equals(obj)) {
					return true;
				}
			}
			if (children.get(i) instanceof TreeObjectDTO && obj instanceof TreeObjectDTO) {
				if (children.get(i).equals(obj)) {
					return true;
				}
			}
		}
		return false;
	}

	public List<TreeObjectDTO> childList(){
		return children;
	}

	public boolean contains(int id) {
		boolean succes = false ;
		for (TreeObjectDTO type : children) {
			if(type instanceof IDirectoryItemDTO && ((IDirectoryItemDTO)type).getDirectoryId()==id){
				succes = true;
			}
		}
		
		return succes;
	}

	public IDirectoryItemDTO getChild(int id) {
		
		for (TreeObjectDTO type : children) {
			if(type instanceof IDirectoryItemDTO && ((IDirectoryItemDTO)type).getDirectoryId()==id){
				return (IDirectoryItemDTO) type;
			}
		}
		
		return null;
	}
	
	public void setAvailableGroups(String[][] avGroups) {
		this.availableGroups = avGroups;
	}

	/**
	 * @return the availableGroups
	 */
	public String[][] getAvailableGroups() {
		return availableGroups;
	}
	
	
	/**
	 * @return the comment
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * @param comment the comment to set
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}

	/**
	 * @return the dateCreation
	 */
	public String getDateCreation() {
		return dateCreation;
	}

	/**
	 * @param dateCreation the dateCreation to set
	 */
	public void setDateCreation(String dateCreation) {
		this.dateCreation = dateCreation;
	}

}
