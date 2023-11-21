package bpm.vanilla.platform.core.beans;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Role {
	private Integer id;
	private String name = "";
	private String comment = "";
	private Date creation = Calendar.getInstance().getTime();
	private String image = "";
	private String type = "";
	private String custom1 = "";
	private String grant = "";
	public static transient SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	public Role(){}

	public Role(String name, int id) {
		this.name = name;
		this.id = id;
	}
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	public void setId(String id){
		try {
			this.id = Integer.parseInt(id);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Date getCreation() {
		return creation;
	}

	public void setCreation(Date creation) {
		this.creation = creation;
	}

	public void setCreation(String date) {
		if (date != null) {
			try {
				this.creation = sdf.parse(date);
			} catch (ParseException e) {
				
			}
		}
	}
	
	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getCustom1() {
		return custom1;
	}

	public void setCustom1(String custom1) {
		this.custom1 = custom1;
	}
	
	public String getGrants(){
		return grant;
	}
	
	public void setGrants(String grants){
		this.grant = grants;
	}
	
	public boolean canCreate(){
		return grant !=  null && grant.contains("C");
	}
	
	public boolean canRead(){
		return grant !=  null && grant.contains("R");
	}
	
	public boolean canUpdate(){
		return grant !=  null && grant.contains("U");
	}
	
	public boolean isAuthorized(){
		return grant !=  null && grant.contains("A");
	}
	
	public boolean canExecute(){
		return grant !=  null && grant.contains("E");
	}
	
	public boolean canDelete(){
		return grant !=  null && grant.contains("D");
	}
	
	public String getXml() {
		StringBuffer buf = new StringBuffer();
		buf.append("    <role>\n");
		if (id != null)
			buf.append("        <id>" +  id + "</id>\n");
		
		buf.append("        <name>" +  name + "</name>\n");
		buf.append("        <comment>" + comment +  "</comment>\n");
		buf.append("        <image>" + image +  "</image>\n");
		buf.append("        <type>" + type +  "</type>\n");
		if (creation != null)
			buf.append("        <creation>" + sdf.format(creation) + "</creation>\n");
		buf.append("        <custom1>" + custom1 +  "</custom1>\n");
		buf.append("        <grant>" + grant +  "</grant>\n");
		buf.append("    </role>\n");
		return buf.toString();
	}
}	
