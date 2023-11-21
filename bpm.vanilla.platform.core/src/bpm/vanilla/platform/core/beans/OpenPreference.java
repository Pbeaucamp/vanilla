package bpm.vanilla.platform.core.beans;


public class OpenPreference {
	private int id;
	private int itemId;
	private String itemName;
	private int userId;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public void setStringId(String id) {
		try{
			this.id = Integer.parseInt(id);
		}
		catch(NumberFormatException e){
			
		}
	}
	
	public int getItemId() {
		return itemId;
	}
	
	public void setItemId(int itemId) {
		this.itemId = itemId;
	}
	
	public void setStringItemId(String itemId) {
		try{
			this.itemId = Integer.parseInt(itemId);
		}
		catch(NumberFormatException e){
			
		}
	}
	
	public String getItemName() {
		return itemName;
	}
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	
	public void setStringUserId(String userId) {
		try{
			this.userId = Integer.parseInt(userId);
		}
		catch(NumberFormatException e){
			
		}
	}
	
	
	public String getXml(){
		StringBuffer buf = new StringBuffer();
		
		buf.append("    <open>\n");
		buf.append("    	<id>" + id + "</id>\n");
		buf.append("    	<itemId>" + itemId + "</itemId>\n");
		buf.append("    	<itemName>" + itemName + "</itemName>\n");
		buf.append("    	<userId>" + userId + "</userId>\n");
		buf.append("    </open>\n");
		
		return buf.toString();
	}

}
