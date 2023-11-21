package bpm.faweb.shared;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

public class FilterConfigDTO implements IsSerializable {

	private List<String> filters;
	private String name = "";
	private String comment = "";
	private int id;
	private Date creationDate;
	private int fasdId;
	private String cubeName;
	
	public FilterConfigDTO() {
		
	}

	public List<String> getFilters() {
		if(filters == null) {
			filters = new ArrayList<String>();
		}
		return filters;
	}

	public void setFilters(List<String> filters) {
		this.filters = filters;
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

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public int getFasdId() {
		return fasdId;
	}

	public void setFasdId(int fasdId) {
		this.fasdId = fasdId;
	}

	public String getCubeName() {
		return cubeName;
	}

	public void setCubeName(String cubeName) {
		this.cubeName = cubeName;
	}

	public void addFilter(String filter) {
		if(filters == null) {
			filters = new ArrayList<String>();
		}
		filters.add(filter);
	}

	public String getXml() {
		StringBuilder buf = new StringBuilder();
		
		buf.append("<filterconfig>\n");
		
		buf.append("	<name>"+name+"</name>\n");
		buf.append("	<comment>"+comment+"</comment>\n");
		buf.append("	<fasdid>"+fasdId+"</fasdid>\n");
		buf.append("	<cubename>"+cubeName+"</cubename>\n");
		buf.append("	<filters>\n");
		
		for(String filter : filters) {
			buf.append("		<filter>"+filter+"</filter>\n");
		}
		
		buf.append("	</filters>\n");
		buf.append("</filterconfig>\n");
		return buf.toString();
	}
	
	
}
