package bpm.vanilla.platform.core.repository;

public class CubeView{

	private String name;
	private int fasdModelId;
	private String cubeName;
	private String description = "";
	private int directoryItemId;
	
	private int id;
	
	private String image;
	private int fasdItemId;
	
	public String getName() {
		return name;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public int getId(){
		return id;
	}
	
	public void setId(String id){
		try{
			this.id = Integer.parseInt(id);
		}catch(NumberFormatException e){
			
		}
		
	}
	
	public String getXml(String xmlView){
		StringBuffer buf = new StringBuffer();
		
		buf.append("<report>\n");
		buf.append("    <document-properties>\n");
		buf.append("        <id>" + id + "</id>\n");
		buf.append("        <name>" + name + "</name>\n");
		buf.append("        <cubename>" + cubeName + "</cubename>\n");
		buf.append("        <fasdid>" + fasdModelId + "</fasdid>\n");
		buf.append("        <description>" + description + "</description>\n");
		buf.append("    </document-properties>\n");
		buf.append(" <fav>\n");
		buf.append("        <name>" + name + "</name>\n");
		buf.append("        <cubename>" + cubeName + "</cubename>\n");
		buf.append("        <fasdid>" + fasdItemId + "</fasdid>\n");
		buf.append( xmlView);
		buf.append(" </fav>\n");
		buf.append("</report>\n");
		
		return buf.toString();
	}

	public int getFasdModelId() {
		return fasdModelId;
	}

	public void setFasdModelId(int fasdModelId) {
		this.fasdModelId = fasdModelId;
	}

	public String getCubeName() {
		return cubeName;
	}

	public void setCubeName(String cubeName) {
		this.cubeName = cubeName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getDirectoryItemId() {
		return directoryItemId;
	}

	public void setDirectoryItemId(int directoryItemId) {
		this.directoryItemId = directoryItemId;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getImage() {
		return image;
	}

	public void setFasdItemId(int id2) {
		this.fasdItemId = id2;
	}

}
